Generate reports with Seam 3 Reports and Apache Velocity
========================================================

 
  Seam 3 provides a collection of CDI extension. Seam 3 report module bridges CDI and several report engines, such as
  
  * [JasperReports](http://jasperforge.org/projects/jasperreports)
  * [Pentaho](http://www.pentaho.com/)
  * [XDocReport](http://code.google.com/p/xdocreport/)
  
  I will demonstrate how to use JasperReports engine to generate PDF document.
  
  Generally, generate a JasperReports report in a Seam 3/Java EE6 project you have to:
  
  * Create JasperRoports jrxml file using iReports or JasperStudio
  * Compile jrxml file to PDF
  
  You can quickly create a project using Forge or reuse sample code from JBoss AS Example, and add seam 3 reports module into your pom.xml.
  
	  <dependency>
		    <groupId>org.jboss.seam.reports</groupId>
		    <artifactId>seam-reports-api</artifactId>
		    <version>${seam-reports-version}</version>
	  </dependency>
	
	  <!-- If you are using Jasper Reports, add the following dependency --> 
	  <dependency>
		    <groupId>org.jboss.seam.reports</groupId>
		    <artifactId>seam-reports-jasper</artifactId>
		    <version>${seam-reports-version}</version>
	  </dependency>
  
  
  In your java code, inject JasperReports compiler to compile the jasperReports source, and JasperReports renderer to render the compiled result.
  
	  @Inject
	  @Jasper
	  private transient ReportCompiler compiler;
	  
	  @Inject
	  @PDF
	  @Jasper
	  private transient ReportRenderer pdfRenderer;
  
  The *@Jasper* annotation states we will use JasperReports as report engine, *@PDF* is the final generated document format.
  
		ReportDefinition report;
		try {
			report = compiler.compile(<JasperReporst source>);
			Report reportInstance = report.fill(<JR DataSource>, null);

			pdfRenderer.render(reportInstance,
					externalContext.getResponseOutputStream());
		} catch (ReportException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
  
  For the JRDataSource, you can specify the JRDataSource you will use in the reports. JasperReports provides several JRDataSource implementation for different cases.
  
  Personally, I dislike the jrxml syntax and do not want to use jrxml syntax to fill data, I only want to use JasperReports as report engine. I am familiar with Apache Velocity, is possible using Velocity as template and fill data? 
  
  Now change the report generation process slightly, and introduce an extra step to generate the pure jrxml.
  
  * Create a velocity template source(embed velocity syntax into jrxml)
  * Convert the velocity template to pure jrxml
  * Compile the jrmxl to PDF.
  
  Create a jrxml firstly, and embed the velocity syntax.
  
  
	  <detail>
			<band height="125" splitType="Stretch">
				#set( $y = 0 )
			    #foreach($obj in $contacts)
				<staticText>
					<reportElement  x="0" y="$y" width="177" height="20"/>
					<textElement textAlignment="Left" verticalAlignment="Middle"/>
					<text><![CDATA[$!obj.name]]></text>
				</staticText>
				<staticText>
					<reportElement  x="177" y="$y" width="200" height="20"/>
					<textElement textAlignment="Left" verticalAlignment="Middle"/>
					<text><![CDATA[$!obj.phoneNumber]]></text>
				</staticText>
				<staticText>
					<reportElement x="377" y="$y" width="178" height="20"/>
					<textElement textAlignment="Left" verticalAlignment="Middle"/>
					<text><![CDATA[$!obj.emailAddress]]></text>
				</staticText>
				#set($y = $y + 20)
				#end
			</band>
		</detail>
  
  In java codes, firstly fill data in the velocity based jrxml, then convert it to pure jrxml, finally compile it to PDF document.
  
  
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
  
  The velocity support is from the Seam3 mail module, Seam 3 has another "renderer" module for this purpose, but it is not released at the moment. So you have to add seam3 mail dependency in your pom.xml.
   
        <dependency>
			<groupId>org.jboss.seam.mail</groupId>
			<artifactId>seam-mail-api</artifactId>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.jboss.seam.mail</groupId>
			<artifactId>seam-mail</artifactId>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.velocity</groupId>
			<artifactId>velocity</artifactId>
		</dependency>
  
  Check out the complete code from github.com, have a try yourself. 
  
  