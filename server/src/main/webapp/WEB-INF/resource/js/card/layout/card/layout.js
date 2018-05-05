var ReactCSSTransitionGroup = require('react-addons-css-transition-group'); // ES5 with npm
import * as cardAction from '../../action/cardAction';
import {Cards} from "./memory/cards";
import {CardList} from "./manage/cardlist";
import {Link} from "react-router";

var generateRandomId = function() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    }).toUpperCase();
}

/**
 * onSave
 * onCancel
 */
const Form = React.createClass({

    getIinitialDatas : function () {
        var data = new Object();
        data.seqId = generateRandomId();
        return new Array(data);
    },

    getInitialState : function () {
        return ({datas : this.getIinitialDatas()});
    },

    handleDelete : function (event, index) {
        const keyCode = event.nativeEvent.code;
        if(keyCode == 'Delete') {
            this.state.datas.splice(index, 1);
            this.setState({datas : this.state.datas});
        }
    },
    handleTab : function (event, index) {
        const keyCode = event.nativeEvent.code;
        if(keyCode == 'Tab') {
            var datas = this.state.datas;
            var data = new Object();
            data.seqId = generateRandomId();
            datas.push(data);
            this.setState({datas : datas});
        }
    },

    handleModify : function (event, data, key) {
        data[key] = event.target.value;
        console.log(this.state.datas);
        this.setState({datas : this.state.datas});
    },

    handleSaveClick : function () {
        var cards = this.state.datas.filter((data) => data.key != null && data.value != null);
        cards = cards.map(data => {
            var card = new Object();
            card.key = data.key;
            card.value = data.value;
            return card;
        });
        this.props.onSave(cards);
        this.setState({datas : this.getIinitialDatas()});
    },

    handleCancelClick : function () {
        this.setState({datas : this.getIinitialDatas()});
        this.props.onCancel();
    },


    renderInput : function () {
        var inputs =this.state.datas.map((data, index) => {

            return (
                <div className="fields">
                    <div className="seven wide field">
                        <textarea rows="3" ref="key" style={{backgroundColor: "rgb(255, 255, 249)"}}
                                  key={data.seqId} value={data.key}
                                  onKeyDown={(event) => this.handleDelete(event, index)}
                                  onChange={(event) => this.handleModify(event,data, "key")}>
                        </textarea>
                    </div>
                    <div className="seven wide field">
                        <textarea rows="3" ref="key" style={{backgroundColor: "rgb(255, 255, 249)"}}
                                  key={data.seqId} value={data.value}
                                  onKeyDown={(event) => {
                                      this.handleDelete(event, index);
                                      index == this.state.datas.length -1 ?
                                          this.handleTab(event, index) : null;
                                  }}
                                  onChange={(event) => this.handleModify(event,data, "value")}>
                        </textarea>
                    </div>
                </div>
            )
        });
        return inputs;
    },


    render : function () {
        return(
            <div className="ui form" style={{padding: "14px", border: "1px solid #ced6d5"}}>
                {this.renderInput()}
                <button className="ui small teal button" onClick={this.handleSaveClick}>提交</button>
                <button className="ui small  button" onClick={this.handleCancelClick}>取消</button>
            </div>
        )
    }

})

const Button = React.createClass({

    getInitialState : function() {
        return ({isInsert : false})
    },

    onInsertClick : function() {
        this.setState({isInsert : true})
    },

    onConfirmClick : function(cards) {
        this.props.insertClick(cards);
        this.setState({isInsert : false});
    },

    onCancelClick : function() {
        this.setState({isInsert : false})
    },

    render : function() {
        return (
            <div>
                <div className="blue ui buttons">
                    <button className="ui small primary button" style={{margin: "10px 0 10px 0"}}
                            onClick={this.onInsertClick}>新增
                    </button>
                    <Link to={`/print/${this.props.brochureId}`} target="_blank">
                        <a className="ui small primary button" style={{margin: "10px 0 10px 0"}}>打印预览</a>
                    </Link>
                </div>
                <ReactCSSTransitionGroup
                    transitionName="example"
                    transitionEnterTimeout={500}
                    transitionLeaveTimeout={300}>
                    {this.state.isInsert &&
                        <Form onSave={this.onConfirmClick} onCancel={this.onCancelClick}/>
                    }
                </ReactCSSTransitionGroup>
            </div>
        )
    }
})

/**
 *
 */
const CardLayout = React.createClass({

    getDefaultProps : function() {
        return ({contents : new Array()})
    },

    getInitialState : function() {
        return (
            {
                contents : this.props.contents,
                memoryCards : new Array(),
                mode : 0                // 0 普通 1 记忆 2 管理
            }
        )
    },

    // 加载数据
    componentDidMount : function() {
        console.log(this.props.location.state.brochure);
        this.listManageContents();
    },

    listMemoryContents : function () {
        cardAction.listRemeber(this.props.location.state.brochure.id,
            (data) => this.setState({memoryCards : data}));
    },

    listManageContents : function () {
        cardAction.list(this.props.location.state.brochure.id,
            (data) => this.setState({contents : data}));
    },

    handleSave : function (card, success) {
        cardAction.save(this.props.location.state.brochure.id, card, this.listManageContents)
    },

    handleSaveList : function (cards, success) {
        cardAction.saveList(this.props.location.state.brochure.id, cards, this.listManageContents)
    },


    handleDelete : function (card, success) {
        cardAction.remove(this.props.location.state.brochure.id, card, this.listManageContents)
    },

    handleSwap : function (firstId, secondId) {
        cardAction.swap(this.props.location.state.brochure.id, firstId, secondId,
            this.listManageContents)
    },

    handleMode : function (mode) {
      if(this.state.mode != mode) {
          this.setState({mode : mode});
      }
    },

    renderMenu : function() {
        return (
            <div className="ui secondary pointing menu">
                <a className={this.state.mode == 0 ? "active item" : "item"}
                   onClick={() => this.handleMode(0)}>
                    卡片记忆
                </a>
                <a className={this.state.mode == 2 ? "active item" : "item"}
                   onClick={() => this.handleMode(2)}>
                    卡片管理
                </a>
            </div>
        )
    },

    renderManage : function() {
        return (
            <div>
                <div>
                    <Button memoryclick={() => (this.setState({mode: 1}))}
                            insertClick={this.handleSaveList}
                            onChange={(data) => (this.setState({contents: data}))}
                            brochureId={this.props.location.state.brochure.id}/>
                </div>
                <div>
                    <div id="card-list">
                        <div className="ui cotainer">
                            <CardList contents={this.state.contents}
                                      onDelete={this.handleDelete}
                                      onSwap = {this.handleSwap}
                                      onChange={(data) => (this.setState({contents: data}))}/>
                        </div>
                    </div>
                </div>
            </div>
        )
    },

    renderMemory : function () {
        return (
            <div>
                <button className="ui primary button"
                        onClick={() => (this.setState({mode : 0}))}>退出</button>
                <Cards contents={this.state.memoryCards} brochure={this.props.location.state.brochure}
                       onComplete={() => (this.setState({mode : 0}))}
                />
            </div>
        )
    },

    renderMemoryHome : function () {
        return (
            <div className="ui fluid card">
                <div className="center aligned content">
                    <h1>{'⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅  '+ this.props.location.state.brochure.name + '  ⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅'}</h1>
                </div>
                <div className="center aligned extra content">
                    <div className="ui grid">
                        <div className="ui three wide computer only column"></div>
                        <div className="ui sixteen wide tablet ten wide computer column ">
                            <button className="ui large teal fluid button"
                                    onClick={() => {
                                        this.listMemoryContents();
                                        this.setState({mode : 1});
                                    }}>开始学习</button>
                        </div>
                        <div className="ui three wide computer only column"></div>
                    </div>
                </div>
            </div>
        )
    },

    render : function() {
        var mode = this.state.mode;
        var content = null;

        switch (mode) {
            case 0 :
                content = this.renderMemoryHome();
                break;
            case 1 :
                content = this.renderMemory();
                break;
            case 2 :
                content = this.renderManage();
                break;
        }

        return (
            <div id="card-box" className="ui container">
                {this.renderMenu()}
                <div id="card-content" className="ui segment">
                    {content}
                </div>
            </div>
        )
    }
})

module.exports.CardLayout = CardLayout;