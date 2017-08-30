$(document).ready(function(e){
	$("#login").click(function(e){

		var data = {
				username:  $("#username").val(),
				password: $("#password").val()
		}
		var jsonString = JSON.stringify(data);
		$.ajax({
			  type: "POST",
			  url: "/ers/authentication/login",
			  data: jsonString,
			  success: function(data, textStatus, xhr){
				  if(xhr.status === 200){
					  successfulLogin(data);
				  }
			  },
			  error: function(xhr, textStatus, errorThrown){
				  if(xhr.status === 401)
					  failedLoggedIn();
			  }
			});
	})
})


function failedLoggedIn(){
	console.log("failed");
	$("#responseText").text("Invalid Credentials. Please try again.");
}
function successfulLogin(data){
	console.log("success");
	$("#responseText").text("Successful Login.");
	var json = $.parseJSON(data);
	var role = json["userRole"]["userRole"]
	if(role == "Employee")
		window.location = "employee_home.html"
	if(role == "Finance Manager")
		window.location = "finance_manager_home.html"
}