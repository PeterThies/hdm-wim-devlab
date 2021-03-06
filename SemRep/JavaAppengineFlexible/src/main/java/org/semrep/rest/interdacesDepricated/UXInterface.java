package org.semrep.rest.interdacesDepricated;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.hdm.wim.sharedLib.Constants;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;


import java.sql.Timestamp;
import org.semrep.rest.businessObjects.Dokumentvorschlag;
import org.semrep.rest.businessObjects.Person;
import org.semrep.rest.helper.FusekiConfigConstants;

/**
 * The type Ux interface.
 */
@Path("/uxInterface")
public class UXInterface {
	
	private static JSONObject jsonObj;
	private static Logger loggger = Logger.getLogger(UXInterface.class.getName());


	/**
	 * The constant filePath.
	 */
// ### initialisiere globale Jena-Variablen
	public static String filePath = "src/semRepServices/interdacesDepricated/Ontology.owl";
	/**
	 * The constant ontologyModel.
	 */
	public static OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	/**
	 * The constant resultSet.
	 */
	public static ResultSet resultSet;
	/**
	 * The constant queryExecution.
	 */
	public static QueryExecution queryExecution;

	/**
	 * The Dok offer linked hash map.
	 */
// ### initialisiere globale HashMaps
	public static LinkedHashMap<String, String> dokOfferLinkedHashMap = null;
	/**
	 * The Alle dokumente linked hash map.
	 */
	public static LinkedHashMap<String, String> alleDokumenteLinkedHashMap = null;
	/**
	 * The Dok offer hash map.
	 */
	public static HashMap<String, String> dokOfferHashMap = null;
	/**
	 * The Tmp dok offer hash map.
	 */
	public static HashMap<String, String> tmpDokOfferHashMap = null;
	/**
	 * The Fav dok hash map.
	 */
	public static HashMap<String, String> favDokHashMap = null;
	/**
	 * The Tmp fav dok hash map.
	 */
	public static HashMap<String, String> tmpFavDokHashMap = null;

	/**
	 * The constant timestamp.
	 */
// ### time stamp
	public static Timestamp timestamp = null;
	private static long timestampLong;

	/**
	 * The constant dokumentvorschlagObj.
	 */
// ### initialisiere globale Objekte
	public static Dokumentvorschlag dokumentvorschlagObj = null;
	/**
	 * The constant personObj.
	 */
	public static Person personObj = null;
	/**
	 * The constant personFavDokObj.
	 */
	public static Person personFavDokObj = null;
	
	// ### initialisiere globale Variablen

	/**
	 * The Input array.
	 */
// inputArray
	public static String[] inputArray = null;

	// Standard Variablen
	private static String sessionIDStr = "";
	/**
	 * The constant eventUniqueID.
	 */
	public static String eventUniqueID = "'null'";
	private static String timeStampStr = "";

	/**
	 * The constant dok_IDStr.
	 */
// Dokument-Objekt bezogen
	public static String dok_IDStr = "";
	/**
	 * The constant dok_NameStr.
	 */
	public static String dok_NameStr = "";
	/**
	 * The constant prioStr.
	 */
	public static String prioStr = "";
	/**
	 * The constant dok_TypStr.
	 */
	public static String dok_TypStr = "";
	/**
	 * The constant dok_URLStr.
	 */
	public static String dok_URLStr = "";
	/**
	 * The constant dok_folder.
	 */
	public static String dok_folder = "";
	/**
	 * The constant personVorname_Str.
	 */
//FavoritDok-Objekt bezogen
	public static String personVorname_Str = "";
	/**
	 * The constant personNachname_Str.
	 */
	public static String personNachname_Str = "";
	/**
	 * The constant personName_Str.
	 */
	public static String personName_Str = "";
	/**
	 * The constant numFavDoks.
	 */
	public static int numFavDoks = 0;

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		// produceUserInformationEvent();
		try {
			getAllDocuments();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets array demo data.
	 */
	public static void setArrayDemoData() {

		// richToken
		inputArray = new String[6];
		inputArray[0] = "1"; // sessionID
		inputArray[1] = "6"; // userID
		inputArray[2] = "HighNet_project"; // context
		inputArray[3] = "milestone"; // keyword
		inputArray[4] = "tasks"; // keyword
		inputArray[5] = "leading"; //keyword
		
		sessionIDStr = inputArray[0].toString();

	}

	/**
	 * Execute sparql.
	 *
	 * @param sparql the sparql
	 */
	public static void executeSparql(String sparql){
		// Initialisierung und Ausführung einer SPARQL-Query
//		Query query = QueryFactory.create(sparql);
//		queryExecution = QueryExecutionFactory.create(query, ontologyModel);
		
		Query query = QueryFactory.create(sparql);
		// queryExecution = QueryExecutionFactory.create(query,
		// ontologyModel);
		queryExecution = QueryExecutionFactory
				.sparqlService(FusekiConfigConstants.FUSEKI_ADDRESS, query);

		// Initialisierung von Resultset für Ergebniswerte der SPARQL-Query
		resultSet = queryExecution.execSelect();
	}

	/**
	 * Loop through results.
	 *
	 * @param y the y
	 */
	public static void loopThroughResults(int y){
		
		// initialisiere Variablen
		String splitResult = "";
		int indexOfToSplitCharacter;
		int countLoop = 0;
		int countDokOffersInLoop = 0;
		
		List<String> splitKeywordsList = null;

		// Ergebniswerte werden für Konsolendarstellung aufbereitet
		for (@SuppressWarnings("unused")
		int i = 0; resultSet.hasNext() == true; i++) {
			countLoop = countLoop + i;
			QuerySolution querySolution = resultSet.nextSolution();
			for (int j = 0; j < resultSet.getResultVars().size(); j++) {
				String results = resultSet.getResultVars().get(j).toString();
				RDFNode rdfNode = querySolution.get(results);

				indexOfToSplitCharacter = rdfNode.toString().indexOf("#");
				splitResult = rdfNode.toString().substring(indexOfToSplitCharacter + 1);

				if (y == 0 || y == 2 || y == 3) {

					// einmaliges befüllen der nachfolgenden Werte
					if (((dokumentvorschlagObj.getDok_IDStr() == "") == true)
							|| ((dokumentvorschlagObj.getDok_NameStr() == "") == true)
							|| ((dokumentvorschlagObj.getDok_TypStr() == "") == true)
							|| ((dokumentvorschlagObj.getDok_URLStr() == "") == true)
							|| ((dokumentvorschlagObj.getDok_folder() == "") == true)) {

						switch (results) {
						case "Dok_ID":
							dok_IDStr = splitResult;
							dokumentvorschlagObj.setDok_IDStr(dok_IDStr);
							break;
						case "Dok_Name":
							dok_NameStr = splitResult;
							dokumentvorschlagObj.setDok_NameStr(dok_NameStr);
							break;
						case "Dok_Typ":
							dok_TypStr = splitResult;
							dokumentvorschlagObj.setDok_TypStr(dok_TypStr);
							break;
						case "Dok_URL":
							dok_URLStr = splitResult;
							dokumentvorschlagObj.setDok_URLStr(dok_URLStr);
							break;
						case "Dok_Ordner":
							dok_folder = splitResult;
							dokumentvorschlagObj.setDok_folder(dok_folder);
							break;
						}

					}

				}
				//ermittle das favorisierte Dokument zu einer Person
				if (y == 1) {
					
					// einmaliges befüllen der nachfolgenden Werte
					if (((personName_Str == "") == true)) {

						switch (results) {
						case "PersonVorname":
							personVorname_Str = splitResult;
							break;
						case "PersonNachname":
							personNachname_Str = splitResult;
							break;
						}
						
						if (personVorname_Str != "" && personNachname_Str != ""){
							personFavDokObj.setName(personVorname_Str + "_" + personNachname_Str);
							personName_Str = personFavDokObj.getName();
						}

					}
									
				}

			}
			
			//ablegen eines vollständigen Objekts in einer der entsprechenden Maps
			if (y == 0) {
				// prio bei allen Dokumenten irrelevant (0)
				prioStr = "0";
				dokumentvorschlagObj.setPrio(prioStr);
				dokOfferLinkedHashMap.put("Dokument" + i,
					Constants.PubSub.AttributeKey.SESSION_ID + ":" + dokumentvorschlagObj.getSessionID() + ", "
						+ Constants.PubSub.AttributeKey.TIMESTAMP + ":"	+ dokumentvorschlagObj.getTimeStamp()
						+ ", " + Constants.PubSub.AttributeKey.TOKEN_ID + ":" + eventUniqueID
						+ ", " + Constants.PubSub.AttributeKey.DOCUMENT_ID + ":"
								+ dokumentvorschlagObj.getDok_IDStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_NAME + ":"
								+ dokumentvorschlagObj.getDok_NameStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_PRIO + ":"
								+ dokumentvorschlagObj.getPrio() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_TYPE + ":"
								+ dokumentvorschlagObj.getDok_TypStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_URL + ":"
								+ dokumentvorschlagObj.getDok_URLStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_FOLDER + ":"
								+ dokumentvorschlagObj.getDok_folder());

				dokumentvorschlagObj.flushDokumentvorschlag();
			} else if (y == 2) {
				// prio bei Dokumentvorschlägen relevant (1)
				prioStr = "1";
				dokumentvorschlagObj.setPrio(prioStr);
				favDokHashMap.put("Favorit_" + countDokOffersInLoop,
					Constants.PubSub.AttributeKey.SESSION_ID + ":" + dokumentvorschlagObj.getSessionID() + ", "
						+ Constants.PubSub.AttributeKey.TIMESTAMP + ":" + dokumentvorschlagObj.getTimeStamp()
						+ ", " + Constants.PubSub.AttributeKey.TOKEN_ID + ":" + eventUniqueID
						+ ", " + Constants.PubSub.AttributeKey.DOCUMENT_ID + ":"
								+ dokumentvorschlagObj.getDok_IDStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_NAME + ":"
								+ dokumentvorschlagObj.getDok_NameStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_PRIO + ":"
								+ dokumentvorschlagObj.getPrio() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_TYPE + ":"
								+ dokumentvorschlagObj.getDok_TypStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_URL + ":"
								+ dokumentvorschlagObj.getDok_URLStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_FOLDER + ":"
								+ dokumentvorschlagObj.getDok_folder());

				countDokOffersInLoop = countDokOffersInLoop + 1;
				dokumentvorschlagObj.flushDokumentvorschlag();
			} else if (y == 3) {
				// prio bei Dokumentvorschlägen relevant (1)
				prioStr = "1";
				dokumentvorschlagObj.setPrio(prioStr);
				dokOfferHashMap.put("Dokumentvorschlag_" + countDokOffersInLoop,
					Constants.PubSub.AttributeKey.SESSION_ID + ":" + dokumentvorschlagObj.getSessionID() + ", "
						+ Constants.PubSub.AttributeKey.TIMESTAMP + ":" + dokumentvorschlagObj.getTimeStamp()
						+ ", " + Constants.PubSub.AttributeKey.TOKEN_ID + ":" + eventUniqueID
						+ ", " + Constants.PubSub.AttributeKey.DOCUMENT_ID + ":"
								+ dokumentvorschlagObj.getDok_IDStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_NAME + ":"
								+ dokumentvorschlagObj.getDok_NameStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_PRIO + ":"
								+ dokumentvorschlagObj.getPrio() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_TYPE + ":"
								+ dokumentvorschlagObj.getDok_TypStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_URL + ":"
								+ dokumentvorschlagObj.getDok_URLStr() + ", " + Constants.PubSub.AttributeKey.DOCUMENT_FOLDER + ":"
								+ dokumentvorschlagObj.getDok_folder());

				countDokOffersInLoop = countDokOffersInLoop + 1;
				dokumentvorschlagObj.flushDokumentvorschlag();
			} 

		}

		queryExecution.close();
		
	}


	/**
	 * Gets document offers.
	 *
	 * @return the document offers
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDocumentOffers")
	public static Response getDocumentOffers() {
		
		//@Path: /rest/uxInterface/produceOfferEvent
		
		
		JSONObject jsonObj = new JSONObject();
		
		setArrayDemoData();

		dokOfferLinkedHashMap = new LinkedHashMap<String, String>();
		dokOfferHashMap = new LinkedHashMap<String, String>();
		tmpDokOfferHashMap = new LinkedHashMap<String, String>();
		favDokHashMap = new LinkedHashMap<String, String>();
		tmpFavDokHashMap = new LinkedHashMap<String, String>();

		timestamp = new Timestamp(System.currentTimeMillis());
		timestampLong = timestamp.getTime();

		try {
			// initialisiere Variablen
			// sparql
			String sparql = "";

			// initialisiere Objekte
			// dokument
			dokumentvorschlagObj = new Dokumentvorschlag(sessionIDStr, String.valueOf(timestampLong), eventUniqueID, dok_IDStr, dok_NameStr, prioStr,
					dok_TypStr, dok_URLStr, dok_folder);
			//favoritDok
			personFavDokObj = new Person(personName_Str);

			for (int z = 0; z <= inputArray.length; z++) {

				if (z == 0) {

					// Alle Dokumente abfragen
					sparql = " PREFIX ontology: <http://www.semanticweb.org/sem-rep/ontology#> "
							+ "SELECT DISTINCT ?Dokument  ?Dok_ID ?Dok_Name ?Dok_Typ ?Dok_URL ?Dok_Ordner " + "WHERE { "
							+ "?Dokument ontology:Dok_ID ?Dok_ID . " + "?Dokument ontology:Dok_Name ?Dok_Name . "
							+ "?Dokument ontology:Dok_Typ ?Dok_Typ . " + "?Dokument ontology:Dok_URL ?Dok_URL . "
							+ "?Dokument ontology:Dok_Ordner ?Dok_Ordner . " + "}";

				}
				//ermittle FavoritDok
				if (z == 1) {
					sparql = "PREFIX ontology: <http://www.semanticweb.org/sem-rep/ontology#> "
							+ "SELECT DISTINCT ?PersonNachname ?PersonVorname "
							+ "WHERE { "
							+ "?Person ontology:Person_Vorname ?PersonVorname . "
							+ "?Person ontology:Person_Nachname ?PersonNachname . "
							+ "?Person ontology:Person_ID '" + inputArray[z].toString() + "' . "
							+ "}";
				}
				if (z == 2) {
	
							sparql = " PREFIX ontology: <http://www.semanticweb.org/sem-rep/ontology#> "
									+ "SELECT DISTINCT ?Kontext ?Dokument ?Dok_ID ?Dok_Name ?Dok_Typ "
									+ "?Dok_URL ?Dok_Keywords ?Dok_Ordner " 
									+ "WHERE { "
									+ "?Dokument ontology:Dok_ID ?Dok_ID . " 
									+ "?Dokument ontology:Dok_Name ?Dok_Name . "
									+ "?Dokument ontology:Dok_Typ ?Dok_Typ . " 
									+ "?Dokument ontology:Dok_URL ?Dok_URL . "
									+ "?Dokument ontology:Dok_Ordner ?Dok_Ordner . "
									+ "?Dokument ontology:Dok_Kontext ?Kontext . "
									+ "?Dokument ontology:Dok_Keywords ?Dok_Keywords . "
									+ "?Dokument ontology:Dokument_favorisiert_von_Person ontology:" + personFavDokObj.getName() +" ."
									+ "}";
					
				}
				if (z == 3) {
					
					sparql = " PREFIX ontology: <http://www.semanticweb.org/sem-rep/ontology#> "
							+ "SELECT DISTINCT ?Kontext ?Dokument  ?Dok_ID ?Dok_Name ?Dok_Typ "
							+ "?Dok_URL ?Dok_Keywords ?Dok_Ordner " + "WHERE { "
							+ "?Dokument ontology:Dok_ID ?Dok_ID . " + "?Dokument ontology:Dok_Name ?Dok_Name . "
							+ "?Dokument ontology:Dok_Typ ?Dok_Typ . " + "?Dokument ontology:Dok_URL ?Dok_URL . "
							+ "?Dokument ontology:Dok_Ordner ?Dok_Ordner . "
							+ "?Dokument ontology:Dok_Kontext ?Kontext . "
							+ "?Dokument ontology:Dok_Keywords ?Dok_Keywords . "
							//+ "?Dokument ontology:Dok_Keywords '" + inputArray[y].toString().toLowerCase() + "' . "
							+ "?Dokument ontology:Dok_Kontext '" + inputArray[2].toString() + "' . "
							+ "FILTER ( "
							+ "?Dok_Keywords = '" + inputArray[3].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[4].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[5].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[4].toString() + " ' && '" + inputArray[3].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[3].toString() + " ' && '" + inputArray[4].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[4].toString() + " ' && '" + inputArray[5].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[5].toString() + " ' && '" + inputArray[4].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[3].toString() + " ' && '" + inputArray[5].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[5].toString() + " ' && '" + inputArray[3].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[4].toString() + " ' && '" + inputArray[3].toString() + " ' && '" + inputArray[5].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[4].toString() + " ' && '" + inputArray[5].toString() + " ' && '" + inputArray[3].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[3].toString() + " ' && '" + inputArray[4].toString() + " ' && '" + inputArray[5].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[3].toString() + " ' && '" + inputArray[5].toString() + " ' && '" + inputArray[4].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[5].toString() + " ' && '" + inputArray[4].toString() + " ' && '" + inputArray[3].toString() + "' || "
							+ "?Dok_Keywords = '" + inputArray[5].toString() + " ' && '" + inputArray[3].toString() + " ' && '" + inputArray[4].toString() + "' "
							+ ") "
							+ "}";

				}
				if (z > 3 && z <= inputArray.length) {
					sparql = "";
				}

				if (sparql != "") {
					
					executeSparql(sparql);
					loopThroughResults(z);

				}

			}

		} catch (Exception e) {
			loggger.error("Fehler in UXInterface: " + e);
		}

		dokumentvorschlagObj.flushAllDokumentvorschlag();

		// ### checke Duplikate ###
		int countOffers = 0;
		String offerStr = "";
		//favoriten
		for (HashMap.Entry<String, String> entry : favDokHashMap.entrySet()) {
			if (!tmpFavDokHashMap.containsValue(entry.getValue())) {
				offerStr = entry.getKey().toString().split("_")[0];
				tmpFavDokHashMap.put(offerStr + countOffers, entry.getValue());
				countOffers = countOffers + 1;
			}
		}
		for (HashMap.Entry<String, String> entry : tmpFavDokHashMap.entrySet()) {
			dokOfferLinkedHashMap.put(entry.getKey(), entry.getValue());
		}
		
		countOffers = 0;
		offerStr = "";
		//dokumentvorschläge
		for (HashMap.Entry<String, String> entry : dokOfferHashMap.entrySet()) {
			if (!tmpDokOfferHashMap.containsValue(entry.getValue())) {
				offerStr = entry.getKey().toString().split("_")[0];
				tmpDokOfferHashMap.put(offerStr + countOffers, entry.getValue());
				countOffers = countOffers + 1;
			}
		}
		for (HashMap.Entry<String, String> entry : tmpDokOfferHashMap.entrySet()) {
			dokOfferLinkedHashMap.put(entry.getKey(), entry.getValue());
		}

		//drucke kompletes OfferEvent aus
		for (String key : dokOfferLinkedHashMap.keySet()) {
			System.out.println(key + ": " + dokOfferLinkedHashMap.get(key) + ", ");
		}

		//return dokOfferLinkedHashMap;
		jsonObj.put("OfferEvent", dokOfferLinkedHashMap.toString());
		return Response.status(200).entity(jsonObj.toString()).build();


	}

	/**
	 * Gets all documents.
	 *
	 * @return the all documents
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllDocuments")
	public static Response getAllDocuments() {
		
		JSONObject jsonObj = new JSONObject();
		
		setArrayDemoData();
		
		dokOfferLinkedHashMap = new LinkedHashMap<String, String>();
		
		timestamp = new Timestamp(System.currentTimeMillis());
		timestampLong = timestamp.getTime();

		try {

			// initialisiere Variablen
			// sparql
			String sparql = "";

			// dokument
			String sessionIDStr = inputArray[0];
			String dok_IDStr = "";
			String dok_NameStr = "";
			String prioStr = "";
			String dok_TypStr = "";
			String dok_URLStr = "";
			String dok_folder = "";

			// initialisiere Objekte
			// dokument
			dokumentvorschlagObj = new Dokumentvorschlag(sessionIDStr, String.valueOf(timestampLong), eventUniqueID, dok_IDStr, dok_NameStr, prioStr,
					dok_TypStr, dok_URLStr, dok_folder);

					// Alle Dokumente abfragen
					sparql = " PREFIX ontology: <http://www.semanticweb.org/sem-rep/ontology#> "
							+ "SELECT DISTINCT ?Dokument  ?Dok_ID ?Dok_Name ?Dok_Typ ?Dok_URL ?Dok_Ordner " + "WHERE { "
							+ "?Dokument ontology:Dok_ID ?Dok_ID . " + "?Dokument ontology:Dok_Name ?Dok_Name . "
							+ "?Dokument ontology:Dok_Typ ?Dok_Typ . " + "?Dokument ontology:Dok_URL ?Dok_URL . "
							+ "?Dokument ontology:Dok_Ordner ?Dok_Ordner . "
							+ "}";

			//execute query and handle results
			executeSparql(sparql);
			loopThroughResults(0);


		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		dokumentvorschlagObj.flushAllDokumentvorschlag();

		// drucke alles im richTokenHashMap aus
		for (String key : dokOfferLinkedHashMap.keySet()) {
			System.out.println(key + ": " + dokOfferLinkedHashMap.get(key) + ", ");
		}

		//return dokOfferLinkedHashMap;
		jsonObj.put("InformationToAllDocumentsEvent", dokOfferLinkedHashMap.toString());
		return Response.status(200).entity(jsonObj.toString()).build();

	}

}
