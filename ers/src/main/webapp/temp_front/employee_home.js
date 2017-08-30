$(document).ready(function() {
	$.ajax({
		type : "GET",
		url : "/ers/content/getPastTickets",
		success : function(data, textStatus, xhr) {
			if (xhr.status === 200) {
				displayReimbursements(data);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			if (xhr.status === 401)
				console.log(textStatus)
		}
	});
})

function displayReimbursements(data) {
	var json = $.parseJSON(data);	
	console.log(json);
	for(key in json){
		id = json[key]["id"]
		amount = json[key]["amount"]
		description = json[key]["description"]
		author = json[key]["author"]["username"]
		resolver = json[key]["resolver"]["username"]
		appendToTable(id,amount,description, author,resolver)
	}

}

function appendToTable(id, amount, description, author, resolver){
	var tr = document.createElement("TR");
	var idCell = document.createElement("TD");
	var amountCell = document.createElement("TD");
	var descCell = document.createElement("TD");
	var authorCell = document.createElement("TD");
	var resolverCell = document.createElement("TD");
	idCell.innerText = id;
	amountCell.innerText = amount;
	descCell.innerText = description;
	authorCell.innerText = author;
	resolverCell.innerText = resolver;
	tr.append(idCell);
	tr.append(amountCell);
	tr.append(descCell);
	tr.append(authorCell);
	tr.append(resolverCell);
	$("#requestTable").append(tr);

}
