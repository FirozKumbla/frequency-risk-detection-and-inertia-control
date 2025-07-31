package com.siemens.proton.hackx.util;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.model.SwitchgearDTO;
import com.siemens.proton.hackx.repository.GridConfigRepository;
import com.siemens.proton.hackx.repository.SwgConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoaderClass implements CommandLineRunner {

    @Autowired
    private GridConfigRepository gridConfigRepository;

    @Autowired
    private SwgConfigRepository swgConfigRepository;

    @Override
    public void run(String... args) throws Exception {

        List<LocationConfigModel> locationList = List.of(LocationConfigModel.builder()
                        .locationName("Location A")
                        .latitude("12.9716")
                        .longitude("77.5946").address("Bangalore, India")
                        .windMillCount(1)
                        .solarPanelCount(15)
                        .build(),
                LocationConfigModel.builder()
                        .locationName("Location B")
                        .latitude("13.0827")
                        .longitude("80.2707")
                        .address("Chennai, India")
                        .windMillCount(2)
                        .solarPanelCount(10)
                        .build());
        gridConfigRepository.saveAll(locationList);

        List<SwitchgearDTO> switchgearList = List.of(
                SwitchgearDTO.builder()
                        .locationId(1)
                        .swgName("Switchgear A")
                        .incomerFeeder(6)
                        .activeIncomerFeeder(4)
                        .outgoingFeeder(6)
                        .activeOutgoingFeeder(4)
                        .build(),
                SwitchgearDTO.builder()
                        .locationId(2)
                        .swgName("Switchgear B")
                        .incomerFeeder(6)
                        .activeIncomerFeeder(4)
                        .outgoingFeeder(6)
                        .activeOutgoingFeeder(4)
                        .build()
        );
        swgConfigRepository.saveAll(switchgearList);
    }
}
