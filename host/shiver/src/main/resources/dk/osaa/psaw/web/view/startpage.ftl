<#-- @ftlvariable name="" type="dk.osaa.psaw.web.api.StartPageData" -->
<html><head><title>Hello there</title></head><body>

Hello ${startPageData.hello?html}

<table>
<#list startPageData.rows as row>
<tr>
  <#list row as col>
   <td>${col?html}</td>
  </#list>
</tr>  
</#list>
</table>

</body></html>