$(document).ready(function() {
	
    $('#userLoginForm').formValidation(userLoginValite);
	
    $('#userRegisterForm').formValidation(userRegisterValidate);
    
    $('#userEditForm').formValidation(userRegisterValidate);
    
    $('#saveAnimeForm').formValidation(animeValidate);
    
    $('#editAnimeForm').formValidation(animeValidate);
    
});

var userLoginValite = extend({
	fields: {
		username: {
			validators: {
				notEmpty: {
					message: '用户名不能为空'
				}
			}
		},
		password: {
			validators: {
				notEmpty: {
					message: '密码不能为空'
				}
			}
		}
	}
}, commonValite)

var userRegisterValidate = extend({
	fields: {
		username: {
            verbose: false,
			validators: {
				notEmpty: {
					message: '用户名不能为空'
				},
				regexp: {
					regexp: '^[a-zA-Z0-9_]{6,18}$',
					message: '用户名只能以数字或字母组合，且长度不能少于6位超过18位'
				},
				remote: {
					url: '/scratch/user/register/user',
					type: 'POST',
					message: '用户名已经存在'
				}
			}
		},
		email: {
			validators: {
				notEmpty: {
					message: '邮箱不能为空'
				},
				regexp: {
					regexp: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
					message: '邮箱格式不正确'
				}
			}
		},
		password: {
			validators: {
				notEmpty: {
					message: '密码不能为空'
				},
				regexp: {
					regexp: /^[a-zA-Z0-9_]{6,18}$/,
					message: '密码只能以数字或字母组合，且长度不能少于6位超过18位'
				}
			}
		},
		rePassword: {
			validators: {
				notEmpty: {
					message: '确认密码不能为空'
				},
				identical: {
					field: 'password',
					message: '两次密码输入不一致'
				}
			}
		}
	}
}, commonValite)


var animeValidate = extend({
    fields: {
    	name: {
            validators: {
                notEmpty: {
                    message: '名称不能为空'
                }
            }
        },
    	pic: {
    		validators: {
    			regexp: {
    				regexp: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/,
    				message: '无效链接'
    			}
    		}
    	}
    }
}, commonValite);

var commonValite = {
	framework: 'bootstrap',
	icon: {
	//	valid: 'glyphicon glyphicon-ok',
	    invalid: 'glyphicon glyphicon-remove',
	    validating: 'glyphicon glyphicon-refresh'
	}	
}


