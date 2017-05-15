
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="modal-header">
	<h4 class="modal-title" id="myModalLabel">站点关联</h4>
</div>

<form class="form-horizontal" action="anime/link"
	id="editAnimeAliasForm" method="post" onsubmit="return false;">
	<div class="modal-body">
		
		<div class="col-sm-offset-1">
		
		<input name="id" type="hidden" value="${anime.id }"/>

		<div class="form-group">
			<label class="control-label">${anime.name }</label>
		</div>
		
		<c:forEach var="host" items="${animeHosts.entrySet() }">
			<div class="form-group">
				<div class="col-sm-2">
					<input type="checkbox" name="checks"/>
					<label class="control-label">${host.value }</label>
					<input type="hidden" name="hostId" value="${host.key }"/>
				</div>
				<div class="col-sm-8">
					<input class="form-control anime-alias" disabled="disabled" name="alias"/>
				</div>
			</div>
		</c:forEach>
		</div>
	</div>
	
	<div class="modal-footer">
		<input type="submit" class="btn btn-primary btn-link" value="保存" />
	</div>
	
</form>


<script type="text/javascript">
var $alias = $("input[type='checkbox'");
$alias.click(function() {

	var $input = $(this).parent().parent().find("input[name='alias']");
	
	if(this.checked) {
		$input.attr("disabled", null);
	} else {
		$input.attr("disabled", "disabled");
	}
})


$("#editAnimeAliasForm").submit(function() {
	
	var array = [];
	var $input = $(".anime-alias");
	
	$input.map(function (index) {
		
		var $item = $($input.get(index));
		
		console.debug($item);
		
		var hostId = $item.parent().parent().find("input[name='hostId']").val();
		var val = $item.val();
		
		console.debug(hostId);
		
		//准备数据		
		if($item.attr("disabled") == "disabled" || val.trim() == "") {
			return null;
		} else {
			array.push({"animeId" : 1, "hostId" :hostId , "alias" : val});
		}
		return null;
	});

	console.debug(array);
	console.debug(JSON.stringify(array));
	
    $.ajax({ 
        type:"POST", 
        url:"anime/link", 
        dataType:"json",      
        contentType:"application/json",               
        data:JSON.stringify(array), 
        success:function(data){ 
        } 
     }); 
	
	return false;
}) 
	

	
</script>
