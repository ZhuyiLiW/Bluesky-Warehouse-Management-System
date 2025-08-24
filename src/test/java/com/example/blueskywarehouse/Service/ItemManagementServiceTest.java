package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.ItemManagementRepository;
import com.example.blueskywarehouse.Entity.Item;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemManagementServiceTest {

    @Mock
    private ItemManagementRepository itemManagementRepository;

    @InjectMocks
    private ItemManagementService itemManagementService;

    @Test
    void addItemTest() {
        // Simuliere das Hinzufügen eines neuen Produkts
        doNothing().when(itemManagementRepository).addItem("name1", "testType", 20, "testGroup");
        ApiResponse<?> response = itemManagementService.addItem("name1", "testType", 20, "testGroup");

        // Überprüfe die Rückmeldung
        assertEquals("Neues Produkt erfolgreich hinzugefügt", response.getMessage());
    }

    @Test
    void updateItemTest() {
        // Erstelle ein Item-Objekt als Rückgabe von findById
        Item item = new Item();
        item.setId(1);
        item.setName("oldName");
        item.setUnitPerBox(10);
        item.setProductGroup("oldGroup");

        // Simuliere Optional.of(item) als Rückgabe von findById
        when(itemManagementRepository.findById(1L)).thenReturn(Optional.of(item));
        // Simuliere save: gibt das übergebene Objekt direkt zurück
        when(itemManagementRepository.save(Mockito.<Item>any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Rufe die Methode auf
        ApiResponse<?> response = itemManagementService.updateItem(1, "name1", "testType", 20, "testGroup");

        // Überprüfe die Rückmeldung
        assertEquals("Produkt erfolgreich aktualisiert", response.getMessage());

        // Überprüfe, ob die Felder aktualisiert wurden
        assertEquals("name1", item.getName());
        assertEquals(20, item.getUnitPerBox());
        assertEquals("testGroup", item.getProductGroup());

        // Verifiziere, dass findById und save aufgerufen wurden
        verify(itemManagementRepository).findById(1L);
        verify(itemManagementRepository).save(item);
    }

    @Test
    void searchItemTest() {
        // Simuliere eine Ergebnisliste von Produkten
        List<Item> mockData = Arrays.asList(
                new Item(1, "name1", "type1", 10, "group1"),
                new Item(2, "name2", "type2", 10, "group2"),
                new Item(3, "name3", "type3", 10, "group3")
        );

        when(itemManagementRepository.searchItem("name")).thenReturn(mockData);
        ApiResponse<?> response = itemManagementService.searchItem("name");
        List<Item> result = (List<Item>) response.getData();

        // Überprüfe Rückmeldung und Inhalte
        assertEquals("Produktdetails erfolgreich abgerufen", response.getMessage());
        assertEquals(3, result.size());
        assertEquals("type2", result.get(1).getType());
    }

    @Test
    void searchItemLocationTest() {
        // Simuliere Lagerorte für ein Produkt
        List<String> mockData = Arrays.asList("location1", "location2", "location3");
        when(itemManagementRepository.searchItemLocation(1)).thenReturn(mockData);

        ApiResponse<?> response = itemManagementService.searchItemLocation(1);
        List<String> result = (List<String>) response.getData();

        assertEquals("Produktstandorte erfolgreich abgerufen", response.getMessage());
        assertEquals(3, result.size());
        assertEquals("location2", result.get(1));
    }


}
