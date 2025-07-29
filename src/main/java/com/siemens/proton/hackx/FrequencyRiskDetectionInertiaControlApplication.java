package com.siemens.proton.hackx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dataexchange.DataExchangeClient;
import software.amazon.awssdk.services.dataexchange.model.DataSetEntry;
import software.amazon.awssdk.services.dataexchange.model.ListDataSetsRequest;
import software.amazon.awssdk.services.dataexchange.model.ListDataSetsResponse;

@SpringBootApplication
public class FrequencyRiskDetectionInertiaControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrequencyRiskDetectionInertiaControlApplication.class, args);

		// Define the region
		Region region = Region.US_EAST_1; // Choose your AWS region

		// Create a DataExchangeClient
		try (DataExchangeClient dataExchangeClient = DataExchangeClient.builder()
				.region(region)
				.build()) {

			// List data sets
			ListDataSetsRequest listRequest = ListDataSetsRequest.builder().build();
			ListDataSetsResponse listResponse = dataExchangeClient.listDataSets(listRequest);

			// Display dataset information
			for (DataSetEntry dataSet : listResponse.dataSets()) {
				System.out.println("DataSet: " + dataSet.name() + ", Id: " + dataSet.id());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
