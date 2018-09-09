var React =  require('react');
var ReactDOM = require('react-dom');
var Table = require("./semantic-react/table").Table;
var Column = require("./semantic-react/table").Column;
var DropDown = require("./semantic-react/table").DropDown;
var Select = require("./semantic-react/table").Select;

$(document).ready(function() {
    dic.render();
    ReactDomRender(<Episode/>, "episodes-box");
    userInfo.render("user-contaienr");
    animeInfo.render("anime-container");
});


var Episode = React.createClass({

    getInitialState : function () {
        return {
            datas : new Array(),
            page : null
        }
    },

    componentWillMount : function () {
        this.animeMap = this.getAnimeMap();
        this.hostMap = this.getHostMap();
    },

    componentDidMount : function () {
        var _this = this;
        Ajax.get("/api/admin/episodes", null, {
            success : function(data) {
                _this.setState({datas : data});
            }
        });
    },

    getHostMap : function () {
        var map = new Object();
        Ajax.get("/api/dics/01", null, {
            async : false,
            success : function (data) {
                map = data;
            }
        });
        return map;
    },

    getAnimeMap : function() {
        var map = new Object();
        Ajax.get("/api/admin/animes", null, {
            async : false,
            success : function(data) {
                data.map(anime => {
                    map[anime.id] = anime.name;
                })
            }
        });
        return map;
    },

    setFliter : function(name, value) {
        if(this.filterAttr == null) {
          this.filterAttr = new Object();
      }
      this.filterAttr[name] = value;
    },

    filter : function () {
        var _this = this;
        Ajax.get("/api/admin/episodes", null, {
            data : this.filterAttr,
            success : function (data) {
                _this.setState({datas : data});
                this.filterAttr = null;
            }
        });
    },

    renderTable : function () {
        return (
            <Table datas={this.state.datas} titles={["名称","集号","链接", "来源","更新时间","推送时间"]}
                   onSave={(data) => Ajax.syncPost("/api/admin/episodes", data)}
                   modify={(data) => Ajax.syncPut("/api/admin/episodes", data)}
                   delete={(data) => Ajax.syncDelete("/api/admin/episodes/" + data.id)}>
                <Column name="anime.id" type="select" map={this.animeMap} width="250px"></Column>
                <Column name="number" width="150px"></Column>
                <Column name="url" width="150px"></Column>
                <Column name="hostId" width="150px" type="select" map={this.hostMap}></Column>
                <Column name="scratchTime" type="datetime" width="150px"></Column>
                <Column name="pushTime" type="datetime" width="150px"></Column>
            </Table>
            )
    },

    renderFilter : function () {
        return(
            <div className="ui six column doubling stackable grid container">
                <div className="two column">
                    <Select name={"animeId"} map={this.animeMap} onChange={(event) => (this.setFliter(event.target.name, event.target.value))} />
                </div>
                <div className="column">
                    <button className="ui small zprimary button" onClick={this.filter} >确定</button>
                </div>


            </div>
        )
    },

    render : function () {
      return (
          <div>
              {this.renderFilter()}
              {this.renderTable()}
          </div>
      )
    }
});

var userInfo = {

    rendernRestButton : function (data) {

        var click = function () {
            if(!confirm("确定重置用户:" + data.username + "的密码？")) return;
            Ajax.put("/api/admin/users/reset/" + data.userId, null, {
                success : function (data) {
                    console.log(data);
                    message("密码重置成功, 初试密码为:" +data.password);
                },
                error : function () {
                    message("密码重置失败");
                }
            });
        }

        return (
            <button className="ui green icon button" onClick={click}>
                <i className="refresh icon"></i>
            </button>
        )
    },

    page : function(pageNo) {
        var result = new Object();
        Ajax.get("/api/admin/users?page=" + pageNo, null,{
            async : false,
            success : function(data) {
                result.data = data.data;
                result.page = {
                    total : data.page.total,
                    current : data.page.current
                }
            }
        });
        return result;
    },

    render : function (id) {
        ReactDomRender(<Table onPage={this.page} title="用户管理" titles={["用户名", "邮箱", "状态", "权限"]}
                              onRenderButtons={this.rendernRestButton}
                              rule = {USER_RULE.ADMIN}
                              modify={(data) => (Ajax.syncPut("/api/admin/users", data))}
                              onSave={(data) => (Ajax.syncPost("/api/admin/users", data))}
                              delete={(data) => (Ajax.syncDelete("/api/admin/users/" + data.userId))}>
            <Column name="username"/>
            <Column name="email"/>
            <Column name="status" defaultValue="1" type="select" map={{"1" : "激活", "0" : "未激活"}}/>
            <Column name="role" defaultValue="0" type="select" map={{"1" : "管理员", "0" : "普通用户"}}/>
        </Table>, id)
    }
}

var animeInfo = {

    map : function () {
        var result;
        Ajax.get("/api/dics/02", null, {
            async : false,
            success : function (data) {
                result = data;
            }
        });
        return result;
    },

    page : function (page) {
        var result = new Object();
        Ajax.get("/api/admin/animes?page=" + page, null, {
            async : false,
            success : function (data) {
                result.data = data.data;
                result.page = new Object();
                result.page = {
                    current : data.page.current,
                    total : data.page.total
                }
            }
        });
        return result;
    },

    renderButtons : function (data) {
        return (
            <span className="ui teal buttons">
                <div className="ui floating dropdown icon button">
                    <i className="dropdown icon"></i>
                    <div className="menu">
                        <div className="item" data-href={CONTEXT + "/admin/anime/upload/" + data.id}>
                            上传图片
                        </div>
                        <div className="item"　data-href={CONTEXT + "/admin/anime/link/" + data.id}>
                            <i className="hide icon"></i>关联
                        </div>
                    </div>
                </div>
            </span>
        )
    },

    // 做一层filter
    render : function (id) {
        ReactDomRender(<Table title="番剧管理"
                              titles={["名称\t", "描述", "开始连载月份", "是否完结", "类型"]}
                              onPage = {this.page} rule = {ANIME_RULE}
                              onSave = {(data) => (Ajax.syncPost("/api/admin/animes", data))}
                              modify= {(data) => (Ajax.syncPut("/api/admin/animes", data))}
                              delete = {(data) => (Ajax.syncDelete("/api/admin/animes/" + data.id))}
                              onRenderButtons={this.renderButtons}
                              onAfterSave = {() => {
                                  initModals();
                                  $('.ui.dropdown').dropdown();
                              }} >
            <Column name="name" width="150px"/>
            <Column name="description" width="150px" type="textarea" />
            <Column name="publishMonth" type="datetime" />
            <Column name="finished" type="select" map={{false : "否", true : "是"}} width="100px"/>
            <Column name="type" type="select" map={this.map()} width="200px"/>
        </Table>, id)
    }
}

var dic = {

    listOptions : function() {
        var result;
        Ajax.get("/api/admin/dics?parentCode=-1", null, {
            async : false,
            success : function(data) {
                result = data;
            }
        })
        return result;
    },

    selectChange : function(event) {
        var parentCode = event.target.value;
        Ajax.get("/api/admin/dics?parentCode="+parentCode, null, {
            success : function (data) {
                dicTable.render(data, parentCode);
            }
        });
    },

    render : function () {
        ReactDomRender(
            <DropDown list={this.listOptions} onChange={this.selectChange}/>, "dic-select")
    }
}

var dicTable = {
    render : function(data, parentCode) {
        ReactDomRender(
            <Table datas={data} dataKeyName={"code"}
                   titles={["编码", "父编码", "值", "序号", "使用状态"]}
                   onSave={(data) => (Ajax.syncPost("/api/admin/dics", data))}
                   modify={(data) => (Ajax.syncPut("/api/admin/dics", data))}
                   delete={(data) => (Ajax.syncDelete("/api/admin/dics/" + data.parentCode + "/" + data.code))}
                   rule={DIC_RULE}>
                <Column name="code" width="150px"></Column>
                <Column name="parentCode" width="150px" canModify={false} defaultValue={parentCode}></Column>
                <Column name="value" width="150px"></Column>
                <Column name="sequence" width="150px"></Column>
                <Column name="used" type={"select"} map={{"1":"使用", "0" : "暫停"}}></Column>
            </Table>, "dic-box");
    }
}

var DIC_RULE = {
    on : 'blur',
    fields : {
        code : {
            rules: [
                {
                    type : 'empty'
                }/*,
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
                }*/
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

var ReactDomRender = function (component, id) {
    var element = document.getElementById(id);
    if(element == null) {
        console.debug("#" + id + "不存在" );
        return;
    }
    ReactDOM.render(component, element);
}
