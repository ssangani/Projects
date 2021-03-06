<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Regular Employee - Authorize Regular Transactions</title>
<script>
        function addRow(tableID) {
 
            var table = document.getElementById(tableID);
            var rowCount = table.rows.length;
            var row = table.insertRow(rowCount);
            var colCount = table.rows[0].cells.length;
 
            for(var i=0; i<colCount; i++) {
                var newcell = row.insertCell(i);
                newcell.innerHTML = table.rows[0].cells[i].innerHTML;
                if(newcell.childNodes[0].type == "label") {
                	newcell.childNodes[0].value = "";
                }
            }
            
            <!-- //#TODO : Add logic to populate data in iterative manner -->
        }
    </script>
</head>

<body>

<input onclick="addRow('dataTable')" type="button" value="Add Row">
<table id="dataTable" border="1" width="750px">
	<tr>
		<th> Name </th>
		<th> Account Number </th>
		<th> Transaction ID </th>
		<th colspan="2"> Decision </th>
	</tr>
	<tr>
		<sf:form action="" method="post">
		<td style="width: 200px; height: 32px;"><label id="NameLabel1">testName</label></td>
		<td style="width: 120px; height: 32px;"><label id="AccNumLabel1">111222000</label></td>
		<td style="width: 120px; height: 32px;"><label id="TIDlabel1">21425366645</label></td>
		<td style="width: 130px; height: 32px;">
		<input name="Accept1" style="width: 130px" type="submit" value="Accept" title="Accept: Commits to Database"></td>
		<td style="height: 32px; width: 120px;">
		<input name="Deny1" style="width: 120px" type="submit" value="Deny" title="Transaction is declined and deleted"></td>
		</sf:form>
	</tr>
</table>

</body>
</html>