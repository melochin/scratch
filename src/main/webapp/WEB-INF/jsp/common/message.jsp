<%@ page isELIgnored="false" %>

<script type="text/javascript">
	$(document).ready(function() {

		var message = "${success }";
		var className = "alert-success";
		if(message == null || message == "") {
			className = "alert-danger";
		}
		$("#messBox").addClass(className);
		console.log(history);
		//setTimeout("jump()", 5000);
	});
		
	function jump() {
		var url = "${jump }";
		if(url == null || url == "") {
			//default use histroy
			history.go(-1);	
		} else {
			//if specified use url
			//window.location = url;
			window.location = new URL(url, document.location);
		}
	}

</script>

<div class="row">
	<div class="col-md-2"></div>
		<div class="col-md-8 msg">
			<div  id="messBox" class="alert alert-autocloseable-info">
				${success}${error }
		    </div>
		</div>
	<div class="col-md-2"></div>
</div>
