<#-- @ftlvariable name="" type="dk.osaa.psaw.web.api.StartPageData" -->
<html><head><title>Hello there</title></head><body>

Hello ${startPageData.hello?html}

<form action="/pt/withfile" method="POST" enctype="multipart/form-data">
	 <input type="file" id="fileUpload" name="file"/>
	 <input type="hidden" name="test" value="fest"/>
     <input type="submit" value="Upload"/>
</form>

<form action="/pt/postage" method="POST" enctype="multipart/form-data">
	 <input type="hidden" name="test1" value="fest1"/>
	 <input type="hidden" name="test2" value="fest2"/>
     <input type="submit" value="No Upload"/>
</form>

</body></html>