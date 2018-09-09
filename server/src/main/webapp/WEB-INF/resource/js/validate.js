$(document).ready(function() {

    $('#userLoginForm').form(USER_RULE.LOGIN);

    $('#userRegisterForm').form(USER_RULE.REGISTER);

    $('#form-dicdata').form(DIC_RULE);

    $('#editAnimeForm').form(ANIME_PIC_RULE);

    // 密码重置
    $('#reset-apply').form(USER_RULE.RESET);

    $('#reset-pwd').form(USER_RULE.RESET_PWD);
/*
    $('#userEditForm').formValidation(userRegisterValidate);
    
    $('#saveAnimeForm').formValidation(animeValidate);
    
    $('#editAnimeForm').formValidation(animeValidate);*/
    
});


/**
 * 表单校验规则
 */

var RULE = new Object();

RULE = {

    USERNAME_NOT_EMPTY : {
        type : 'empty',
        prompt : '用户名不能为空'
    },
    USERNAME_FORM : {
        type: 'regExp',
        value: '^[a-zA-Z0-9_]{6,18}$',
        prompt: '用户名只能以数字或字母组合，且长度不能少于6位超过18位'
    },
    USERNAME_REPEAT : {
        type : 'ajax',
        value : {
            action : 'validate user data',
            type : "post"
        },
        prompt : '用户名已经存在'
    },

    EMAIL_FORM : {
        type : 'email',
        prompt : "无效邮箱地址"
    },

    PASSWORD_NOT_EMPTY : {
        type : 'empty',
        prompt : '密码不能为空'
    },
    PASSWORD_FORM : {
        type : 'regExp',
        value : '^[a-zA-Z0-9_]{6,18}$',
        prompt : '密码只能以数字或字母组合，且长度不能少于6位超过18位'
    }
}

var USER_RULE = {

    ADMIN : {
        on : 'blur',
        fields : {
            username : {
                rules : [
                    RULE.USERNAME_NOT_EMPTY,
                    RULE.USERNAME_FORM
                ]
            },
            email : {
                rules : [RULE.EMAIL_FORM]
            }
        }
    },


	LOGIN : {
        transition : 'fade up',
        fields : {
            username : {
                rules: [RULE.USERNAME_NOT_EMPTY]
            },
            password : {
                rules: [RULE.PASSWORD_NOT_EMPTY]
            }
        }
	},

    RESET : {
        on : 'blur',
        inline : true,
        fields : {
            username : {
                rules: [RULE.USERNAME_NOT_EMPTY]
            },
            email : {
                rules: [RULE.EMAIL_FORM]
            }
        }
    },

    RESET_PWD : {
        on : 'blur',
        inline : true,
        fields : {
            password : {
                rules: [
                    RULE.PASSWORD_NOT_EMPTY,
                    RULE.PASSWORD_FORM
                ]
            },

            repassword : {
                rules : [
                    {
                        type : "match[password]",
                        prompt : "两次输入密码不一致"
                    }
                ]
            }
        }
    },

	REGISTER : {
		on : "blur",
        inline : true,
        fields : {
            username : {
                rules: [
                    RULE.USERNAME_NOT_EMPTY,
                    RULE.USERNAME_FORM,
                    RULE.USERNAME_REPEAT
                ]
            },

            email : {
                rules : [RULE.EMAIL_FORM]
            },

            password : {
                rules: [
                    RULE.PASSWORD_NOT_EMPTY,
                    RULE.PASSWORD_FORM
                ]
            },

            rePassword : {
                rules : [
                    {
                        type : "match[password]",
                        prompt : "两次输入密码不一致"
                    }
                ]
            }
		}
	}
}

var DIC_RULE = {
    on : 'blur',
    inline:true,
    fields : {
        code : {
            rules: [
                {
                    type : 'empty'
                },
                {
                    type : 'ajax',
                    value : {
                        action : 'validate dictionary code',
                        elements : {
                            code: $('#form-dicdata').find("input[name='code']"),
                            parentCode: $('#form-dicdata').find("input[name='parentCode']")
                        }
                    },
                    prompt : '编码已经存在'
                }
            ]
        },
        value : {
            rules: [{type : 'empty'}]
        },
        sequence : {
            rules: [{type : 'number'}]
        }
    }
}


var ANIME_PIC_RULE = {
    on : 'blur',
    inline:true,
    fields : {
        picFile : {
            rules: [{
                type : 'regExp',
                value : '\w*(png|jpg|jpeg)$',
                prompt : '请上传png或者jpg文件'
            }]
        }
    }
}

var ANIME_RULE = {
    on : 'blur',
    fields : {

        name : {
            rules: [
                {
                    type : 'empty',
                    prompt : '名称不能为空'
                }
            ]
        },

        type : {
            rules: [{type : 'empty', prompt : '类型不能为空'}]
        }

    }
}


/*

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
    	},
    	picFileText: {
    		validators: {
    			regexp: {
    				regexp: /\w+.(png|jpg)$/,
    				message: '只支持png、jpg格式的文件'
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
*/


