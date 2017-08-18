/**
 * 表单校验规则
 */

// 用户规则
var userRule = {
	on : 'blur',
	inline : true,
	fields : {

		username : {
			rules: [
				{
					type : 'empty'
				},
				{
					type : 'regExp',
					value : '^[a-zA-Z0-9_]{6,18}$',
					prompt : '用户名只能以数字或字母组合，且长度不能少于6位超过18位'
				},
				{
				  type : 'ajax',
				  value : {
					  action : 'validate user data'
				  	},
			  	prompt : '用户名已经存在'
			  }
			]
		},

		email : {
			rules: [{
				type : 'email'
			}]
		},

		password : {
			rules: [
					{
						type : 'empty'
					},
	        {
						type : 'regExp',
						value : '^[a-zA-Z0-9_]{6,18}$',
						prompt : '密码只能以数字或字母组合，且长度不能少于6位超过18位'
	        }
			]
		}

	}
};

// anime 规则
var animeRule = {
	on : 'blur',
	inline : true,
	fields : {
		
		name : {
			rules: [
				{
					type : 'empty'
				}
			]
		},

		type : {
			rules: [{
				type : 'empty'
			}]
		},

		picFile : {
			rules: [{
				type : 'regExp',
				// 条件表达
				value : '\w*(png|jpg)$',
				prompt : '请用.png结尾的图片上传'
			}]
		}

	}
}

// animeEpisode规则
var animeEpisodeRule = {
	on : 'blur',
	inline : true,
	fields: {
		number : {
			rules: [{
				type : 'empty'
			}]
		},
		url : {
			rules: [
			    {type: 'empty'},
			    {type: 'url'}
			]
		}
	}
}

$(document).ready(function() {
	// 表单校验绑定
	$("#animeEpisode").form(animeEpisodeRule);
});