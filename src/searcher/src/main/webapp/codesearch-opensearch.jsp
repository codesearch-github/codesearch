<%@ page contentType="application/opensearchdescription+xml" %><?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
   <ShortName>Codesearch at ${pageContext.request.serverName}</ShortName>
   <Description>Use Codesearch to search your version control repositories.</Description>
   <Tags>code search developers tools software engineering web</Tags>
   <Contact>codesearch.team@gmail.com</Contact>
   <Url type="text/html" template="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/#search:term={searchTerms}"/>
</OpenSearchDescription>