package org.dice_research.sask.executer_ms.threading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RIOT;
import org.apache.log4j.Logger;
import org.dice_research.sask.executer_ms.workflow.Operator;
import org.dice_research.sask.executer_ms.workflow.Workflow;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * This class is a thread task and responsible for data extraction.
 * 
 * @author André Sonntag
 *
 */

public class ExtractTask implements Runnable {

	private final Logger logger = Logger.getLogger(ExtractTask.class.getName());
	private final RestTemplate restTemplate;
	private final Operator op;
	private final Workflow wf;
	private final String extractorInput;

	public ExtractTask(RestTemplate restTemplate, Workflow wf, Operator op, String extractorInput) {
		this.restTemplate = restTemplate;
		this.wf = wf;
		this.op = op;
		this.extractorInput = extractorInput;
	}

	@Override
	public void run() {
		logger.info("Start Thread: " + ExtractTask.class.getName() + " with Extractor: " + getOperatorName());
		String uri = this.getExtractorURI(this.getOperatorName());

		HttpHeaders headers = new HttpHeaders();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("input", extractorInput);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(uri + "/extractSimple?", request, String.class);
		String extractorOutput = response.getBody();
		
		if (this.getOperatorName().equalsIgnoreCase("FOX-MS")) {

			InputStream in = new ByteArrayInputStream(extractorOutput.getBytes());
			RIOT.init();
			Model model = ModelFactory.createDefaultModel();
			model.read(in, null, "TURTLE");
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutputStream baos = new ByteArrayOutputStream();
			model.write(baos, "N-TRIPLES");
			extractorOutput = baos.toString();
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (this.getNextOperatorList().size() != 0) {
		Set<Runnable> nextOperatorList = TaskFactory.createTasks(this.restTemplate, this.wf, this.getNextOperatorList(),
				new String[] { extractorOutput });
		TaskExecuter executer = new TaskExecuter(nextOperatorList);
		executer.execute();
		}
	}

	private String getOperatorName() {
		return this.op.getContent();
	}

	private Set<Operator> getNextOperatorList() {
		return this.wf.getNextOperators(op);
	}

	private String getExtractorURI(String extractor) {
		String uri;

		switch (extractor) {
		case "CEDRIC-MS":
			uri = "http://CEDRIC-MS";
			break;
		case "FOX-MS":
			uri = "http://FOX-MS";
			break;
		case "FRED-MS":
			uri = "http://FRED-MS";
		case "OPEN-IE-MS":
			throw new RuntimeException("Unsupported extractor '" + extractor + "'");
		case "SPOTLIGHT-MS":
			throw new RuntimeException("Unsupported extractor '" + extractor + "'");
		default:
			throw new RuntimeException("Unsupported extractor '" + extractor + "'");
		}
		return uri;
	}

}
