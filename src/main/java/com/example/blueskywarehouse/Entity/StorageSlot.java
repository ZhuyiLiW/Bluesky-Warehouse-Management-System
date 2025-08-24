package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_slot")
public class StorageSlot {

    @Id
    @Column(name = "bin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bin_code")
    private String binCode;

    @Column(name = "slot_code")
    private String slotCode;

    @Column(name = "pallet_id")
    private Long palletId;

    // 1 is available, 0 is not available
    @Column(name = "is_available")
    private Integer isAvailable;

    @Column(name = "version")
    private Integer version;


    public StorageSlot() {}

    public StorageSlot(Long id, String binCode, String slotCode, Long palletId, Integer isAvailable, Integer version) {
        this.id = id;
        this.binCode = binCode;
        this.slotCode = slotCode;
        this.palletId = palletId;
        this.isAvailable = isAvailable;
        this.version = version;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBinCode() { return binCode; }
    public void setBinCode(String binCode) { this.binCode = binCode; }

    public String getSlotCode() { return slotCode; }
    public void setSlotCode(String slotCode) { this.slotCode = slotCode; }

    public Long getPalletId() { return palletId; }
    public void setPalletId(Long palletId) { this.palletId = palletId; }

    public Integer getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Integer isAvailable) { this.isAvailable = isAvailable; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
