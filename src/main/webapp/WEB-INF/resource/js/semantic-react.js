var Table = require("./semantic-react/table").Table;
var Column = require("./semantic-react/table").Column;
var DropDown = require("./semantic-react/table").DropDown;

$(document).ready(function() {
    dic.render();

    ReactDomRender(<Episode/>, "episodes-box");
    //episode.render("episodes-box");
    userInfo.render("user-contaienr");
    animeInfo.render("anime-container");
});


var Episode = React.createClass({

    getInitialState : function () {
        return {
            datas : new Array(),
            page : null,
            filter : new Object()
        }
    },

    componentWillMount : function () {
        this.animeMap = this.getAnimeMap();
    },

    componentDidMount : function () {
        var _this = this;
        $.ajax("/api/admin/episodes", {
            async : false,
            success : function(data) {
                _this.setState({datas : data});
            }
        });
    },

    getAnimeMap : function() {
        var map = new Object();
        $.ajax("/api/admin/animes", {
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
        $.ajax("/api/admin/episodes", {
            data : this.filterAttr,
            success : function (data) {
                _this.setState({datas : data});
                this.filterAttr = null;
            }
        });
    },

    renderTable : function () {
        return (
            <Table datas={this.state.datas} titles={["名称","集号","链接", "更新时间","推送时间"]}
                   onSave={(data) => Ajax.syncPost("/api/admin/episodes", data)}
                   modify={(data) => Ajax.syncPut("/api/admin/episodes", data)}
                   delete={(data) => Ajax.syncDelete("/api/admin/episodes/" + data.id)}>
                <Column name="anime.id" type="select" map={this.animeMap}></Column>
                <Column name="number"></Column>
                <Column name="url"></Column>
                <Column name="scratchTime" type="datetime"></Column>
                <Column name="pushTime"></Column>
            </Table>
            )
    },

    renderFilter : function () {
        return(
            <div className="ui six column doubling stackable grid container">
                <div className="two column">
                    <Select name={"animeId"} map={this.animeMap}
                            onChange={(event) => (this.setFliter(event.target.name, event.target.value))} />
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


/*var episode = {

  map : function() {
    var map = new Object();
    $.ajax("/api/admin/animes", {
      async : false,
      success : function(data) {
        data.map(anime => {
          map[anime.id] = anime.name;
        })
      }
    });
    console.debug(map);
    return map;
  },

  list : function(func) {
    $.ajax("/api/admin/episodes", {
      success : function(data) {
        func(data);
      }
    });
  },
  filter : function(animeId) {
    var result
    $.ajax("/api/admin/episodes", {
      data: "animeId=" + animeId,
      async : false,
      success: function (data) {
          result = data;
      }
    });
    return result;
  },
  render : function(id) {
    var element = document.getElementById(id);
    if(element == null) return;
    var map = this.map();
    ReactDomRender(
        <Table list={this.list} titles={["名称","集号","链接", "更新时间","推送时间"]}
               save={(data) => Ajax.syncPost("/api/admin/episodes", data)}
               modify={(data) => Ajax.put("/api/admin/episodes", data)}
               delete={(data) => Ajax.delete("/api/admin/episodes/" + data.id)}>
        <Column name="anime.id" type="select" map={map}></Column>
        <Column name="number"></Column>
        <Column name="url"></Column>
        <Column name="scratchTime"></Column>
        <Column name="pushTime"></Column>
    </Table>, id)
  }

}*/

var userInfo = {

    rendernRestButton : function (data) {

        var click = function () {
            if(!confirm("确定重置用户:" + data.username + "的密码？")) return;
            // 调用重置服务
            Ajax.put("/api/admin/users/reset/" + data.userId);
        }

        return (
            <button className="ui green icon button" onClick={click}>
                <i className="refresh icon"></i>
            </button>
        )
    },

    page : function(pageNo) {
        var result = new Object();
        $.ajax("/api/admin/users?page=" + pageNo, {
            async : false,
            success : function(data) {
                result.data = data.data;
                result.page = {
                    total : data.page.totalPage,
                    current : data.page.curPage
                }
            }
        });
        return result;
    },

    render : function (id) {
        ReactDomRender(<Table onPage={this.page} title="用户管理" titles={["用户名", "邮箱", "状态", "权限"]}
                              renderButtons={this.rendernRestButton}
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
        $.ajax("/api/dics/02", {
            async : false,
            success : function (data) {
                result = data;
            }
        });
        return result;
    },

    page : function (page) {
        var result = new Object();
        $.ajax("/api/admin/animes?page=" + page, {
            async : false,
            success : function (data) {
                result.data = data.data;
                result.page = new Object();
                result.page = {
                    current : data.page.curPage,
                    total : data.page.totalPage
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
                        <div className="item" data-href={"/admin/anime/upload/" + data.id}>上传图片</div>
                        <div className="item"　data-href={"/admin/anime/link/" + data.id}><i class="hide icon"></i>关联</div>
                    </div>
                </div>
            </span>
        )
    },

    // 做一层filter
    render : function (id) {
        ReactDomRender(<Table titles={["名称\t", "描述", "开始连载月份", "是否完结", "类型"]}
                              onPage = {this.page} rule = {ANIME_RULE}
                              onSave = {(data) => (Ajax.syncPost("/api/admin/animes", data))}
                              modify= {(data) => (Ajax.syncPut("/api/admin/animes", data))}
                              delete = {(data) => (Ajax.syncDelete("/api/admin/animes/" + data.id))}
                              onRenderButtons={this.renderButtons}>
            <Column name="name"/>
            <Column name="description"/>
            <Column name="publishMonth" type="datetime"/>
            <Column name="finished" type="select" map={{false : "否", true : "是"}}/>
            <Column name="type" type="select" map={this.map()}/>
        </Table>, id)
    }
}

var dic = {

    listOptions : function() {
        var result;
        $.ajax("/api/admin/dics?parentCode=-1", {
            async : false,
            success : function(data) {
                result = data;
            }
        })
        return result;
    },

    selectChange : function(event) {
        $.ajax("/api/admin/dics?parentCode="+event.target.value, {
            success : function (data) {
                dicTable.render(data);
            }
        });
    },

    render : function () {
        ReactDomRender(
            <DropDown list={this.listOptions} onChange={this.selectChange}/>, "dic-select")
    }
}

var dicTable = {
    render : function(data) {
        ReactDomRender(
            <Table datas={data} dataKeyName={"code"}
                   titles={["编码", "父编码", "值", "序号", "使用状态"]}
                   onSave={(data) => (Ajax.syncPost("/api/admin/dics", data))}
                   modify={(data) => (Ajax.syncPut("/api/admin/dics", data))}
                   delete={(data) => (Ajax.syncDelete("/api/admin/dics/" + data.parentCode + "/" + data.code))}
                   rule={DIC_RULE}>
                <Column name="code"></Column>
                <Column name="parentCode"></Column>
                <Column name="value"></Column>
                <Column name="sequence"></Column>
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
