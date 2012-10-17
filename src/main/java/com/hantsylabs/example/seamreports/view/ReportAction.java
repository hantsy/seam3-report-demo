package com.hantsylabs.example.seamreports.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import net.sf.jasperreports.engine.JREmptyDataSource;

import org.jboss.seam.mail.templating.velocity.CDIVelocityContext;
import org.jboss.seam.mail.templating.velocity.VelocityTemplate;
import org.jboss.seam.reports.Report;
import org.jboss.seam.reports.ReportCompiler;
import org.jboss.seam.reports.ReportDefinition;
import org.jboss.seam.reports.ReportRenderer;
import org.jboss.seam.reports.exceptions.ReportException;
import org.jboss.seam.reports.jasper.annotations.Jasper;
import org.jboss.seam.reports.output.PDF;
import org.jboss.solder.resourceLoader.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hantsylabs.example.seamreports.model.Contact;

/**
 * Session Bean implementation class ReportAction
 */
@Stateful
@RequestScoped
@Named(value = "reportAction")
public class ReportAction {
	private static final Logger log = LoggerFactory
			.getLogger(ReportAction.class);

	@Inject
	private ResourceProvider resourceProvider;

	@Inject
	FacesContext facesContext;

	@Inject
	transient CDIVelocityContext velocityContext;

	@Inject
	@Jasper
	private transient ReportCompiler compiler;
	// @Inject
	// @XLS
	// @Jasper
	// private transient ReportRenderer xslRenderer;
	@Inject
	@PDF
	@Jasper
	private transient ReportRenderer pdfRenderer;
	
	@Inject
	List<Contact> contacts;

	/**
	 * Default constructor.
	 */
	public ReportAction() {
	}

	public void render() {
		if (log.isDebugEnabled()) {
			log.debug("export as pdf");
		}
		final String mimeType = "application/pdf";
		final String attachFileName = "contacts.pdf";
		final String reportTemplate = "/contacts.jrxml";

		if (log.isDebugEnabled()) {
			log.debug("mimeType@" + mimeType);
			log.debug("attachFileName@" + attachFileName);
		}

		ExternalContext externalContext = facesContext.getExternalContext();

		externalContext.setResponseContentType(mimeType);
		externalContext.addResponseHeader("Content-Disposition",
				"attachment;filename=" + attachFileName + "");

		InputStream sourceTemplate = resourceProvider
				.loadResourceStream(reportTemplate);

		Map<String, Object> _values = new HashMap<String, Object>();
		_values.put("contacts", contacts);
		_values.put("usd", "$");

		String stringReport = new VelocityTemplate(sourceTemplate,
				velocityContext).merge(_values);
		
		if (log.isDebugEnabled()) {
			log.debug("report source file content@" + stringReport);
		}
		// source
		ReportDefinition report;
		try {
			report = compiler.compile(new ByteArrayInputStream(stringReport
					.getBytes("UTF-8")));
			Report reportInstance = report.fill(new JREmptyDataSource(), null);

			pdfRenderer.render(reportInstance,
					externalContext.getResponseOutputStream());
		} catch (ReportException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		facesContext.responseComplete();

	}

	public void render2() {

	}

}
