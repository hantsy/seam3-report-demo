#Generate reports with Seam 3 Reports and Apache Velocity
 
Seam 3 provides a collection of standard CDI extensions. Seam3 report module bridges CDI and several report engines, such as
  
  * [JasperReports](http://jasperforge.org/projects/jasperreports)
  * [Pentaho](http://www.pentaho.com/)
  * [XDocReport](http://code.google.com/p/xdocreport/)

 
##Basic Configuration

Assume you have already created a Maven based Java EE 6 application. If not, you can create one using JBoss Forge.

Add Seam 3 reports related dependencies into your *pom.xml*.
  
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
  
Generally, in order to generate a JasperReports based report in a Seam 3/Java EE6 project, you could consider the following steps:
  
  1. Create JasperRoports jrxml file using iReports or JasperStudio
  2. Compile jrxml file to PDF
 
You can use the official JaperReports Studio(for Eclipse users) or iReports(for NetBeans users) to create the reports template source file.
  
In your java code, inject JasperReports compiler to compile the jasperReports source, and JasperReports renderer to render the compiled result.
  
	  @Inject
	  @Jasper
	  private transient ReportCompiler compiler;
	  
	  @Inject
	  @PDF
	  @Jasper
	  private transient ReportRenderer pdfRenderer;
  
The `@Jasper` annotation states we will use JasperReports as report engine, `@PDF` is the final generated document format.
  
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
  
`JRDataSource` is a JasperReports specified "datasource" which is responsible of gathering the data for report generation. JasperReports provides several implementations natively, please refer to the JRDataSource javadoc for more detailed info. In this case, we have got a list, we can use `JRBeanCollectionDataSource` to wrap the existing list and use it in this reports. 
  
Everything works well. 

But personally, I dislike the jrxml syntax and do not want to use jrxml syntax to fill data, I only want to use JasperReports as report engine. I am familiar with Apache Velocity, is possible using Velocity as template and fill data? 
  
##Improve the codes with Apache Veloctiy 
  
Now change the report generation process slightly, and introduce an extra step to generate the pure jrxml.
  
  1. Create a velocity template source(embed velocity syntax into jrxml)
  2. Convert the velocity template to pure jrxml
  3. Compile the jrmxl to PDF.
 
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
  
Note, the velocity support is from the Seam3 mail module.
 
In the Seam3 repository, there is a standalone `renderer` module for rendering template, but it is not released a stable version at the moment. 
 
So you have to add Seam3 mail dependencies in your *pom.xml*.
   
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
 
##Run the project 

I assume you have installed the latest Oracle JDK 7, JBoss AS 7.1.1.Final and Apache Maven 3.0.4.
  
  1. Check out the complete codes from github.com. 
  
  		git clone git://github.com/hantsy/seam3-report-demo.git
  	
  2. Start JBoss AS from command line.
  	
  		<JBOSS_HOME>\bin\standalone.bat
  	
  3. Deploy the application into the running JBoss AS.
  
  		mvn clean package jboss-as:deploy
  	
  4. Open your browser and go to http://localhost:8080/seam3-reports-demo.
  	
  	
  
  
  