var moment = require('moment');

const ORIGIN = "origin";
const MODIFY = "modify";
const NEW = "new";
const NEW_MODIFY = "newmodify";

var generateRandomId = function() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    }).toUpperCase();
}

var ObjectTool = {
    setPropoerty : function(obj, propertys, value) {
        if(obj == null) {
            throw "obj is null";
        }
        var nextObj = obj;
        var props = propertys.split(".");
        console.debug("setPropoerty:", props);
        for(var i=0; i<props.length; i++) {
            var prop = props[i];
            if(i == props.length -1) {
                nextObj[prop] = value;
            } else {
                if(nextObj[prop] == null) {
                    nextObj[prop] = new Object();
                }
                nextObj = nextObj[prop];
            }
        }
        console.debug("setPropoerty:", obj);
    },

    getProperty : function(obj, propertys) {
        if(obj == null) {
            throw "obj is null";
        }
        var nextObj = obj;
        var props = propertys.split(".");
        for(var i=0; i<props.length; i++) {
            var prop = props[i];
            if(i != props.length-1) {
                if(nextObj[prop] == null) {
                    throw prop + "is null";
                }
            }
            nextObj = nextObj[prop];
        }
        return nextObj;
    }
}

var cloneObj = function(obj){
    var str, newobj = obj.constructor === Array ? [] : {};
    if(typeof obj !== 'object'){
        return;
    } else if(window.JSON){
        str = JSON.stringify(obj), //系列化对象
            newobj = JSON.parse(str); //还原
    } else {
        for(var i in obj){
            newobj[i] = typeof obj[i] === 'object' ?
                cloneObj(obj[i]) : obj[i];
        }
    }
    return newobj;
};

var Datas = {
    find : function(datas, keyValue, keyName) {
        if(datas == null) return -1;
        if(!Array.isArray(datas)) {
            throw "Datas is not a array";
        }
        for(var index of datas.keys()) {
            if(datas[index][keyName] == keyValue) {
                return index;
            }
        }
        return -1;
    }
}

function DoubleData(datas, keyName) {

    var map = new Map();
    var bakMap = new Map();

    datas.map((data) => {
        let key = data[keyName];
        map.set(key, data);
        bakMap.set(key, cloneObj(data));
    });


    var method = {
        // 从备份中同步数据
        sync : function(key) {
            let value = bakMap.get(key);
            map.set(key, cloneObj(value));
        },

        get : function(key) {
            return map.get(key);
        },

        set : function(key, data) {
            map.set(key, data);
            bakMap.set(key, cloneObj(data));
        },

        setCurrent : function(key, data) {
            map.set(key, data);
        },

        array : function() {
            return map.values();
        },

        remove : function(key) {
            console.debug(key);
            map.delete(key);
            bakMap.delete(key);
        },

        values : function(key) {
            return map.values();
        },

        map : function() {
            return map;
        }

    }

    return method;
}



/*
* datas(array) : 填充表格的数据集
* primaryKey : 数据的主键名
* save  : function(data) data 某行对应的数据
* modify : function(data)
* delete : function(data)
*/
var Table = React.createClass({

    getDefaultProps : function() {
        return {
            titles : new Array(),
            datas : new Array(),
            onSave : function(data) {return true;},
            modify : function(data) {return true;},
            delete : function(data) {return true;},
            onRenderButtons : function(data){return null},
            page : {current : 1, total : 1},
            displayEditButtons : true
        }
    },

    getInitialState : function() {

        this.init();

        var datas = this.props.datas;
        var doubleDatas = null;
        if(datas.length > 0 ) {
            doubleDatas = this.initDoubleData(datas);
        }

        return {
            page : this.props.page,
            doubleDatas : doubleDatas,
            hasNewRow : false
        }
    },

    // 初始化字段属性
    init : function() {
        // 判断是否拥有props primaryKey，如果没有开启自动生成key
        this.autoKey = !this.props.hasOwnProperty("primaryKey");
        // 获取Children Columns
        this.columns = this.getColumns();
    },

    /*获取Columns子组件*/
    getColumns : function () {
        var columns = new Array();
        React.Children.map(this.props.children, function(child) {
            if(child.type.displayName == "Column") {
                columns.push(child);
            }
        });
        return columns;
    },

    // 初始化数据集
    // 状态、主键
    initDoubleData : function(datas) {
        var initDatas = datas.map((data) => {
            data._status = ORIGIN;
            data._key = this.getKey(data);
            return data;
        });
        return DoubleData(initDatas, "_key");
    },

    componentDidMount : function() {
        var _this = this;

        // 装载完毕加入按键监听事件
        var addKeyListener = function() {
            document.addEventListener("keydown", function(event) {
                if(event.ctrlKey && event.code == "KeyI") {
                    _this.insertRow();
                }
            });
        }
        addKeyListener();

        // 读取API数据
        var loadDatas = function() {
            if(_this.state.doubleDatas == null) {
                if(_this.props.hasOwnProperty("onList")) {
                    _this.handleList();
                    return;
                }
                if(_this.props.hasOwnProperty("onPage")) {
                    _this.handlePage("1");
                }
            }
        }
        loadDatas();
    },

    componentWillReceiveProps : function(nextProps) {
        this.setState({doubleDatas : this.initDoubleData(nextProps.datas)});
    },

    getKey : function(data) {
        if(!this.autoKey && data._status != NEW) {
            return data[this.props.primaryKey];
        } else {
            return generateRandomId();
        }
    },

    handleDelete : function(data, key) {

        if(!confirm("确认删除？")) return;

        var success = true;
        success = this.props.delete(data);

        if(!success) {
            alert("删除失败");
            return;
        }

        this.state.doubleDatas.remove(data._key);
        this.setState({doubleDatas : this.state.doubleDatas});
    },

    // Table 不应该关心数据的状态
    handleModify : function(data, key) {
        var modifyData = this.state.doubleDatas.get(data._key);
        modifyData._status = MODIFY;

        this.state.doubleDatas.setCurrent(data._key, modifyData);
        this.setState({doubleDatas : this.state.doubleDatas});
    },

    handleSave: function(data, index) {
        var success = true;
        if(data._status == NEW || data._status == NEW_MODIFY) {
            success = this.props.onSave(data);
        } else {
            success = this.props.modify(data);
        }

        if(!success) {
            alert("保存失败");
            return;
        }

        if(data._status == NEW || data._status == NEW_MODIFY) {
            data._key = this.getKey(data);
            this.setState({hasNewRow : false});
        }

        data._status = ORIGIN;
        this.state.doubleDatas.set(data._key, data);
        this.setState({doubleDatas : this.state.doubleDatas});
    },

    handleCancel : function(data, index) {
        // 新增数据：删除新增行
        if(data._status == NEW || data._status == NEW_MODIFY) {
            this.setState({hasNewRow : false});
        }
        // 已存在的修改数据：恢复修改之前的数据
        if(data._status != NEW && data._status != NEW_MODIFY) {
            this.state.doubleDatas.sync(data._key);
            this.setState({doubleDatas : this.state.doubleDatas});
        }
    },

    handleList : function() {
        var result = this.props.onList();
        var doubleDatas = this.initDoubleData(result);
        this.setState({doubleDatas : doubleDatas});
    },

    handlePage : function(pageNo) {
        var result = this.props.onPage(pageNo);
        var page = result.page;
        var doubleDatas = this.initDoubleData(result.data);
        this.setState({
            doubleDatas : doubleDatas,
            page : page
        });
    },

    insertRow : function() {
        if(this.state.hasNewRow == true) return;
        this.setState({hasNewRow : true});
    },

    renderColumnTitles : function() {
        var titles = this.props.titles;
        return titles.map((data) => (
            <th className="single line">{data}</th>
        ));
    },

    renderInsertButton : function() {
        if(!this.props.displayEditButtons) return null;
        return (
            <button className="ui teal icon button"
                    disabled={this.state.hasNewRow ? "disabled" : ""}
                    onClick={this.insertRow}>
                <i className="ui plus icon"></i>
            </button>
        );
    },

    renderRow : function(data) {
        return <Row data={data}
                    key={data._key}
                    status={data._status}
                    onDeleteClick={this.handleDelete}
                    onModifyClick={this.handleModify}
                    onSaveClick={this.handleSave}
                    onCancelClick={this.handleCancel}
                    onRenderButtons={this.props.onRenderButtons}
                    displayEditButtons={this.props.displayEditButtons}
                    rule={this.props.rule}
                    columns = {this.columns}
        />
    },

    renderRows : function() {
        if(this.state.doubleDatas == null) return null;
        var rows = new Array();
        this.state.doubleDatas.map().forEach((value, key, map) => {
            rows.push(this.renderRow(value));
        });
        return rows;
    },

    renderNewRow : function() {
        if(!this.state.hasNewRow) return null;
        var data = new Object();
        data._status = NEW;
        data._key = this.getKey(data);
        return this.renderRow(data);
    },

    render : function () {
        var columnTiles = this.renderColumnTitles();
        var insertButton = this.renderInsertButton();
        var rows = this.renderRows();
        var newRow = this.renderNewRow();

        console.debug("Table render");
        return (
            <div ref="container">
                <h3>{this.props.title}</h3>
                <Page onPage={this.handlePage} current={this.state.page.current}
                      total={this.state.page.total} />
                <table className="ui celled compact table">
                    <thead>
                    <tr className="form">
                        {columnTiles}
                        <th>
                            {insertButton}
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {newRow}
                    {rows}
                    </tbody>
                </table>
            </div>)
    }
});

var Row = React.createClass({

    getInitialState : function() {
        return (
            {
                data : this.props.data,
                status : this.props.status
            }
        );
    },

    componentWillReceiveProps : function(nextProps) {
        this.setState({
            data : nextProps.data,
            status : nextProps.status
        })
    },

    shouldComponentUpdate(nextProps, nextState){
        if(nextProps.data != this.props.data || nextProps.status != this.props.status) {
            console.debug("ComponentUpdate\n",
                "nextProps:",nextProps, "\n",
                "this.props:", this.props);
            return true;
        }
        return false;
    },

    componentDidMount : function() {
        this.needValidate = false;
        if(this.props.hasOwnProperty("rule") && this.props.rule != undefined) {
            this.needValidate = true;
            var rule = this.props.rule;
            rule.onInvalid = function(formErrors) {
                if($(this).popup("exists")) {
                    $(this).popup("change content", formErrors);
                } else {
                    $(this).popup({
                        content : formErrors,
                        on : 'manual',
                        inline: true});
                }
                $(this).popup("show");
            };
            rule.onValid = function() {
                if($(this).popup("exists")) {
                    $(this).popup("hide");
                }
            }
            $(this.refs.tr).form(this.props.rule);
        }
    },

    deleteClick : function() {
        this.props.onDeleteClick(this.state.data, this.props.key);
    },

    modifyClick : function() {
        this.props.onModifyClick(this.state.data, this.props.key);
    },

    saveClick : function () {
        if(!this.isValidate()) return;
        this.props.onSaveClick(this.state.data, this.props.key);
    },

    cancelClick : function () {
        $(document).popup("hide all");
        if(this.state.status == NEW ||this.state.status == NEW_MODIFY) {
            this.props.onCancelClick(this.props.data, this.props.key);
            return;
        }
        // 如果没有发生变化
        // 则取老的值进行覆盖
        this.props.onCancelClick(this.props.data, this.props.key);
    },

    isValidate : function() {
        if(!this.needValidate) {
            return true;
        }
        $(this.refs.tr).form("validate form");
        return $(this.refs.tr).form("is valid");
    },

    change : function (event, value) {
        var name = event.target.name;
        if(value == null) {
            value = event.target.value;
        }
        var status = this.state.status;

        if(status == NEW) {
            status = NEW_MODIFY;
        }

        // 这里变更值

        this.setState((prevState, props) => {
            var data = prevState.data;
            ObjectTool.setPropoerty(data, name, value);
            console.debug("change data", data);
            return ({
                data : data,
                status : status
            });
        });
    },

    renderRow : function() {
        var status = this.state.status;
        var data = this.state.data;
        var _this = this;
        console.debug("Row renderRow:", data);
        console.debug(status);
        return this.props.columns.map((col) => {
                if(status == NEW) {
                    // 给对象赋值，更新对象，保存的时候才能获取到该默认值
                    ObjectTool.setPropoerty(data, col.props.name, col.props.defaultValue);
                }
                var value = ObjectTool.getProperty(data, col.props.name);
                // 准备组件的参数
                var props = {
                    value : value,
                    status : status,
                    readOnly : (status == MODIFY
                        || status == NEW
                        || status == NEW_MODIFY) ? false : true,
                    onChange : _this.change
                }
                // Column
                var cell = React.cloneElement(col, props);
                return (
                    <td className="collapsing">
                        <div className="field">
                            {cell}
                        </div>
                    </td>)
            }
        );
    },

    renderButtons : function() {
        var status = this.state.status;
        var buttons = new Array();

        if(this.props.displayEditButtons) {
            if (status == MODIFY || status == NEW || status == NEW_MODIFY) {
                buttons.push(
                    <button className="ui orange icon button" onClick={this.saveClick}>
                        <i className={status == NEW || status == NEW_MODIFY ?
                            "add circle icon" : "save icon"}></i>
                    </button>
                );
                buttons.push(
                    <button className="ui icon button" onClick={this.cancelClick}>
                        <i className="undo icon"></i>
                    </button>
                );
            }
            if (status != MODIFY && status != NEW && status != NEW_MODIFY) {
                buttons.push(
                    <button className="ui blue icon button" onClick={this.modifyClick}>
                        <i className="edit icon"></i>
                    </button>
                );
                buttons.push(
                    <button className="ui red icon button" onClick={this.deleteClick}>
                        <i className="trash icon"></i>
                    </button>
                );
            }
        }
        buttons.push(
            this.props.onRenderButtons(this.state.data)
        );
        return (
            <td>
                {buttons}
            </td>
        );
    },

    render : function() {
        console.debug("render row:",this.state.data);
        return(
            <tr ref="tr">
                {this.renderRow()}
                {this.renderButtons()}
            </tr>);
    }
});

var Input = React.createClass({

    getDefaultProps : function() {
      return {
        border : false
      }
    },

    getInitialState : function() {
        return {
            value : this.props.value
        }
    },

    handleChange : function(event) {
        if(this.props.onChange != null) {
            this.props.onChange(event);
        }
        this.setState({value: event.target.value});
    },

    handleBlur : function(event) {
        if(this.props.onBlur != null) {
            this.props.onBlur(event);
        }
        this.setState({value: event.target.value});
    },

    componentWillReceiveProps : function(nextProps) {
        if(this.state.value != nextProps.value) {
            this.setState({value : nextProps.value});
        }
    },

    render : function() {
        var style = this.props.style;

        var displayBorder = !this.props.readOnly || this.props.border;

        if(!displayBorder) {
            if(style == null) style = new Object();
            style.border = "1px solid rgba(0, 0, 0, 0.0)";
        }

        return (
            <input value = {this.state.value} name={this.props.name}
                   type={this.props.type}
                   onChange={this.handleChange} onBlur={this.handleBlur}
                   readOnly={this.props.readOnly && "readOnly"}
                   style = {style}/>
        )
    }
});

var Select = React.createClass({

    getDefaultProps : function() {
        return {value : ""}
    },

    getInitialState : function() {
        return {value : this.props.value}
    },

    handleChange : function(event) {
        console.debug(event);
        console.debug(this.props.onChange);
        if(this.props.onChange != null) {
            this.props.onChange(event);
        }
    },

    componentWillReceiveProps : function(nextProps) {
        if(this.state.value != nextProps.value) {
            this.setState({value : nextProps.value});
        }
    },

    componentDidMount : function() {
        var _this = this;
        $(_this.refs.select).dropdown({
            onChange : function(value, text, $selectItem) {
                var event = {
                    target : {
                        value : value,
                        name : _this.props.name
                    }
                };
                _this.handleChange(event);
            }
        });
    },

    componentDidUpdate : function(prevProps, prevState) {
        // 刷新dropdown选中的值
        $(this.refs.select).dropdown("set selected", this.state.value);
    },

    renderItems : function() {
        var items = new Array();
        for(var i in this.props.map) {
            var item = <div className="item" data-value={i}>{this.props.map[i]}</div>
            items.push(item);
        }
        return items;
    },

    getStyle : function() {
        var style = this.props.style;
        if(style == null) {
            style = new Object();
        }
        style.minWidth = "8rem";
        /*    style.padding = "0px";
            style.border = "0px 3px";
            style.maxWidth = "5rem";*/
        return style;
    },

    render : function() {
        var style = this.getStyle();
        return (
            <div ref="select" className={"ui selection dropdown search fluid " +
            (this.props.readOnly && "disabled") } style={style} name={this.props.name} >
                <input type="hidden" name={this.props.name} value={this.state.value} />
                <div className="default text"></div>
                <i className="dropdown icon"></i>
                <div className="menu">
                    {this.renderItems()}
                </div>
            </div>
        )
    }
})


/*
* name : 指定列的属性名（对象的属性，配合table才有用）
* displayName : 指定列的显示名（即列名）
* type : input/select/
* value : 显示值
* defaultValue ： 默认值（新增时）
* textplacehoder :
* canModify : true/ false true:不能变更值
*
*/
var Column = React.createClass({

    getDefaultProps : function () {

        return {
            readOnly : true,
            canModify : true,
            defaultValue : ''
        }
    },

    render : function () {
        var readOnly = this.props.readOnly;
        var status = this.props.status;
        var value = this.props.value;
        var width = this.props.width;
        if(!this.props.canModify) {
            readOnly = true;
        }

        if(this.props.type == "select") {
            return (
                <Select value={value} name={this.props.name} map={this.props.map} style={{maxWidth : width}}
                        readOnly={readOnly} onChange={this.props.onChange} />
            )
        } else if(this.props.type == "datetime") {
          return <Datetime value={value}
                    readOnly = {readOnly}  style={{maxWidth : width}}
                    name={this.props.name} onChange={this.props.onChange} />
        } else if(this.props.type == "textarea") {
          return <TextArea value={value}
                    readOnly = {readOnly} style={{maxWidth : width}}
                    name={this.props.name} onChange={this.props.onChange} />
        } else {
            return(
                <div className="ui small input" style={{maxWidth : width}}>
                  <Input value={value} name={this.props.name} type={this.props.type}
                         readOnly={readOnly} onBlur={this.props.onChange}/>
                </div>
            )
        }
    }
});

var TextArea = React.createClass({

  getInitialState : function () {
    return {
      value : this.props.value
    }
  },

  componentWillReceiveProps : function(nextProps) {
    this.setState({value : nextProps.value});
  },

  handleClick : function() {
    if(this.props.readOnly) return;
    // 打开modal
    $(this.refs.modal).modal('show');
  },

  handleChange : function (event) {
    this.props.onChange(event);
    this.setState({value : event.target.value});
  },

  renderInput : function() {
    return(
      <div className="ui small icon input" style={this.props.style} onClick={this.handleClick}>
        <Input value={this.state.value} name={this.props. name}
               readOnly={true} border={!this.props.readOnly}/>
          {!this.props.readOnly && <i className="edit outline icon"></i>}
      </div>
    )
  },

  renderModal : function () {
    return (
      <div className="ui modal" ref="modal">
        <div className="content">
          <div className="ui form">
            <textarea name={this.props.name}
              value={this.state.value} onChange={this.handleChange}></textarea>
          </div>
        </div>
      </div>
    )
  },

  render : function() {
    return (
      <div>
        {this.renderInput()}
        {this.renderModal()}
      </div>
    )
  }
});

/*
* 考虑Format
*/
var Datetime = React.createClass({

    handleChange : function(event) {
        var value = moment(event.target.value).format("x");
        this.props.onChange(event, value);
    },

    // 需要更改调整时间格式
    render : function() {
        console.debug(this.props.value);
        var value = moment(parseInt(this.props.value)).format("YYYY-MM-DDTHH:mm");
        return(
            <div className="ui small input" style={this.props.style}>
                <Input type="datetime-local" value={value} name={this.props.name}
                       readOnly={this.props.readOnly} onBlur={this.handleChange}/>
            </div>
        )
    }

});

/*
* total : 总页数
* current : 当前页
*/
var Page = React.createClass({

    getDefaultProps : function() {
        return {
            onPage : function(page) {return true;},
            current : 1,
            total : 1
        }
    },

    getInitialState : function() {
        return {
            current : this.props.current,
            total : this.props.total
        }
    },

    componentWillReceiveProps : function(nextProps) {
        if(nextProps.total != this.state.total) {
            this.setState({total : nextProps.total});
        }
        this.setState({current : nextProps.current});
    },

    renderItems : function() {
        var items = new Array();
        for(let i=1; i<= this.state.total; i++) {
            if(i == this.state.current) {
                items.push(<a className="active item">{i}</a>)
            } else {
                items.push(<a className="item" onClick={()=>{this.onClick(i)}}>{i}</a>)
            }
        }
        return items;
    },

    onClick : function(page) {
        if(page < 1) return;
        if(page > this.state.total) return;
        if(!this.props.onPage(page)) return;
        this.setState({current : page});
    },

    render : function() {
        if(this.state.current == 1 && this.state.total == 1) return null;

        return (
            <div className="ui right floated pagination mini menu" style={{margin : '5px 0'}}>
                <a className="icon item" onClick={() => {this.onClick(this.state.current - 1)}}>
                    <i className="left chevron icon"></i>
                </a>
                {this.renderItems()}
                <a className="icon item" onClick={() => {this.onClick(this.state.current + 1)}}>
                    <i className="right chevron icon"></i>
                </a>
            </div>
        )
    }
})


var DropDown = React.createClass({

    componentWillMount : function() {
        var lists = this.props.list();
        var options = lists.map((option, index) => {
            return <option value={option.code }>{option.code + "-" + option.value}</option>
        })
        this.setState({options : options});
    },

    componentDidMount : function() {
        var $select = $(this.refs.select)
        $select.dropdown();
        var $options = $select.find("option");
        var value = null;
        if($options.length > 0) {
            value = $options.get(0).value;
        }
        // 生产event target的方法不合理，临时解决方法
        if(value != null) {
            this.handleChange({
                target : {
                    value : value
                }
            });
        }
    },

    handleChange : function(event) {
        console.debug(event);
        this.props.onChange(event);
    },

    render : function() {
        var options = this.state.options;
        return(
            <select ref="select" className="ui search dropdown" onChange={this.handleChange}
                    onLoad={this.handleChange}>
                {options}
            </select>
        );
    }
});

module.exports.Table = Table;
module.exports.Column = Column;
module.exports.DropDown = DropDown;
module.exports.Select = Select;
module.exports.Page = Page;