package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;

import gec.scf.file.example.SupplierIdFinderExampleTest;
import gec.scf.file.example.domain.SponsorDataReference;
import gec.scf.file.example.domain.SponsorDocument;

public class DataMergeTest {

	@InjectMocks
	private DataMerge dataMerge;

	@Test
	public void given_list_data_reference_find_supplier_id_00031311_when_merge_document_then_result_document_supplider_id_00031311()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, ParseException {

		// Arrange
		List<DataReference> dataReferenceList = new ArrayList<DataReference>();
		SponsorDataReference dataReference = new SponsorDataReference();
		dataReference.setFieldName("supplierId");
		SupplierIdFinderExampleTest supplierIdFinder = new SupplierIdFinderExampleTest();
		dataReference.setDataFinder(supplierIdFinder);
		dataReferenceList.add(dataReference);

		SponsorDocument document = new SponsorDocument();
		dataMerge = new DataMerge(dataReferenceList);

		// Actualt
		document = dataMerge.merge(document, SponsorDocument.class);

		// Assert
		assertEquals("00031311", document.getSupplierId());
	}
}
