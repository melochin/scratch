var Remarkable = require('remarkable');
var ReactCSSTransitionGroup = require('react-addons-css-transition-group'); // ES5 with npm
var Cards = require('./card/cards').Cards;
import {Dropdown} from 'semantic-ui-react';


// This values are default
var md = new Remarkable({
  html:         false,        // Enable html tags in source
  xhtmlOut:     false,        // Use '/' to close single tags (<br />)
  breaks:       false,        // Convert '\n' in paragraphs into <br>
  langPrefix:   'language-',  // CSS language prefix for fenced blocks
  linkify:      false,        // Autoconvert url-like texts to links
  typographer:  false,        // Enable smartypants and other sweet transforms
  // Highlighter function. Should return escaped html,
  // or '' if input not changed
  highlight: function (/*str, , lang*/) { return ''; }
});

var Brouchures = React.createClass({

    getInitialState : function () {
        return {
          brouchures : new Array(),
          searchOptions : new Array(),
          showForm : false
        }
    },

    componentDidMount : function () {
      this.list();
    },

    list : function () {
        var _this = this;
        Ajax.get("/api/brochures", null, {
            success : function (brochures) {
                var options = brochures.map(brochure => {
                    var option = new Object();
                    option.text = brochure.name;
                    option.value = brochure;
                    return option;
                });
                _this.setState({
                    brouchures : brochures,
                    searchOptions : options});
            }
        })
    },

    delete : function (brochure) {
        var _this = this;
        Ajax.delete("/api/brochures/" + brochure.id, null, {
            success : function() {
                _this.list();
            }
        })
    },

    save : function (brochure) {
        var _this = this;
        Ajax.post("/api/brochures", brochure, {
            success : function () {
                _this.setState({showForm : false});
                _this.list();
                message("新增成功");
            }
        });
    },

    modify : function (brochure, success) {
        var _this = this;
        Ajax.put("/api/brochures", brochure, {
            success : function () {
                _this.list();
            }
        })
    },

    renderBrouchure : function () {
      var _this = this;
      return this.state.brouchures.map(brochure => {
          return(
              <Brochure key={brochure.id} brochure={brochure} delete={_this.delete} modify={_this.modify}/>
          )
      });
    },

    render : function () {
        return (
            <div className="ui container" id="card-box" >
              <div className="ui grid">
{/*                  <div className="ui three wide column">
                      <Dropdown search fluid selection options={this.state.searchOptions} />
                  </div>*/}
                  <div className="ui six wide tablet two wide computer column">
                      <button className="ui primary button"
                              onClick={() => !this.state.showForm && this.setState({showForm : true})}>新增</button>
                  </div>
                  { this.state.showForm &&
                      <div className="ui five wide column">
                          <BrochureForm onSubmit={this.save}
                                        onCancel={() => this.state.showForm && this.setState({showForm : false})}/>
                      </div>
                  }
              </div>
                  <div className="ui five stackable cards">
                    {this.renderBrouchure()}
                 </div>
            </div>
        )
    }

});

/**
 * props : brochure
 *         delete(brochure)
 * @type {*|Function}
 */
var Brochure = React.createClass({

    getInitialState : function () {
        return {
            modify : false,
        }
    },

    componentWillReceiveProps : function (nextProps) {
        this.setState({modify : false})
    },

    renderDefault : function (brochure) {
        return (
            <div className="content">
                <div className="center aligned header">
                    <h1>{brochure.name}</h1>
                </div>
                <div className="center aligned description">
                    <p>{brochure.description == null ? '无' : brochure.description}</p>
                </div>
                <div className="extra content">
                    <div className="center aligned">
                        <button className="ui teal button" onClick={() =>
                            ReactDOM.render(<Box brochure={brochure}/>, document.getElementById("card"))}>开始学习</button>
                    </div>
                </div>
            </div>
        )
    },

    renderModify : function (brochure) {
        return (
            <div className="content ui form">
                <div className="center aligned header">
                    <input ref="name"></input>
                </div>
                <div className="center aligned description">
                    <input ref="description"></input>
                </div>
                <div className="extra content">
                    <div className="center aligned">
                        <button className="ui primary mini button" onClick={() => {
                            var submitBrochure = this.props.brochure;
                            submitBrochure.name = this.refs.name.value;
                            submitBrochure.description = this.refs.description.value;
                            this.props.modify(submitBrochure);
                        }}>保存</button>
                        <button className="ui mini button" onClick={() => this.setState({modify : false})}>取消</button>
                    </div>
                </div>
            </div>
        )
    },


    render : function () {
        var brochure = this.props.brochure;
        var content = this.state.modify ? this.renderModify(brochure) : this.renderDefault(brochure);
        return (
            <div className="ui raised card " key={brochure.id}>
                <div className="right aligned meta" >
                    <i className="large edit icon" onClick={() => this.setState({modify : !this.state.modify})}></i>
                    <i className="large trash alternate icon" onClick={() => this.props.delete(brochure)}></i>
                </div>
                {content}
            </div>
        );
    }

})

/**
 * 册子——新增表单
 *
 * props: onSubmit(brouchure)   表单提交的处理
 *        onCancel()            表单取消的处理
 *
 */
var BrochureForm = React.createClass({

    handleSubmit : function() {
        var brouchure = new Object();
        brouchure.name = this.refs.name.value ;
        brouchure.description = this.refs.description.value;
        this.props.onSubmit(brouchure);
    },

   render : function () {
       return (
           <div id="brochure-form" className="ui form" >
               <div className="two fields">
                   <div className="field">
                       <label>名字</label>
                       <input ref="name"/>
                   </div>
                   <div className="field">
                       <label>描述</label>
                       <input ref="description"/>
                   </div>
               </div>
               <button className="ui primary button" onClick={this.handleSubmit}>提交</button>
               <button className="ui button" onClick={this.props.onCancel}>取消</button>
           </div>
       )
   }
});


var Button = React.createClass({

  getInitialState : function() {
    return ({
      isInsert : false
    })
  },

  onInsertClick : function() {
    this.setState({isInsert : true})
  },

  onConfirmClick : function() {
    var key = this.refs.key.value;
    var value = this.refs.value.value;
    var card = {
      key : key,
      value : value
    }
    var _this = this;
    this.props.insertClick(card, function (data) {
        _this.props.onChange(data);
    });
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
            </div>
            <ReactCSSTransitionGroup
                transitionName="example"
                transitionEnterTimeout={500}
                transitionLeaveTimeout={300}>
                {this.state.isInsert &&
                <div className="ui form" style={{maxWidth: "14rem", padding: "14px", border: "1px solid #ced6d5"}}>
                    <div className="field">
                        <label>正面</label>
                        <textarea rows="3" ref="key" style={{backgroundColor: "rgb(255, 255, 249)"}}></textarea>
                    </div>
                    <div className="field">
                        <label>反面</label>
                        <textarea rows="3" ref="value" style={{backgroundColor: "rgb(255, 255, 249)"}}></textarea>
                    </div>
                    <button className="ui small teal button" onClick={this.onConfirmClick}>提交</button>
                    <button className="ui small button" onClick={this.onCancelClick}>取消</button>
                </div>
                }
            </ReactCSSTransitionGroup>
        </div>
    )
  }
})



var CardList = React.createClass({

  renderCard : function(card) {
    return (
          <div className="item">
              <div className="content">
                  <div className="description">
                      <span dangerouslySetInnerHTML={{__html:md.render(card.key)}}/>
                  </div>
              </div>
              <div className="content">
                  <div className="description">
                      <span dangerouslySetInnerHTML={{__html:md.render(card.value)}} />
                  </div>
                  <button className="ui right floated icon button"
                          style={{background : "white"}} onClick={() => this.props.onDelete(card, null)}>
                      <i className="trash outline icon"></i>
                  </button>
              </div>
          </div>
    )
  },

  componentDidMount : function() {
    $('.ui.accordion').accordion();
  },


  render : function() {
    var cards = this.props.contents.map((content) => (this.renderCard(content)));
    return (
      <div className="ui divided items">
        {cards}
      </div>
    )
  },
})

var Box = React.createClass({

  getDefaultProps : function() {
    var contents = new Array();
    return ({
      contents : contents
    })
  },

  getInitialState : function() {
    return (
      {
        contents : this.props.contents,
        memoryContents : new Array(),
        mode : 0    // 0 普通 1 记忆 2 管理
      }
    )
  },

  // 加载数据
  componentDidMount : function() {
    this.listManageContents();
  },

  listMemoryContents : function () {
      var _this = this;
      Ajax.get("/api/brochures/" + this.props.brochure.id + "/cards/memory", null, {
          success : function (data) {
              _this.setState({memoryContents : data});
          }
      });
  },

  listManageContents : function () {
      var _this = this;
      Ajax.get("/api/brochures/" + this.props.brochure.id + "/cards", null, {
          success : function (data) {
              _this.setState({contents : data});
          }
      });
  },

  openCardStream : function () {
      var _this = this;
      var last_response_data;

      var source= new EventSource(CONTEXT + "/api/stream/brochures/" + this.props.brochure.id +  "/cards");
      source.onmessage=function(event)
      {
          console.debug("event source");
          if(event.data != last_response_data) {
              console.debug("refresh");
              _this.setState({contents : JSON.parse(event.data)})
              last_response_data = event.data;
          }
      };
  },

  handleSave : function (card, success) {
    var _this = this;
    Ajax.post("/api/brochures/" + this.props.brochure.id + "/cards", card, {
        success : function () {
            _this.listManageContents();
        }
    });
  },

  handleDelete : function (card, success) {
    var _this = this;
    Ajax.delete("/api/brochures/" + this.props.brochure.id + "/cards", card, {
      success : function () {
          _this.listManageContents();
      }
    });
  },

  renderMenu : function() {
      return (
          <div className="ui menu">
              <div className="item" onClick={() => ReactDOM.render(<Brouchures />, document.getElementById("card"))}>
                  首页
              </div>
              <a className="item" onClick={() => this.setState({mode : 2})}>
                  卡片管理
              </a>
          </div>
      )
  },

  renderCardList : function() {
    return (
        <div>
            <div>
                <Button memoryclick={() => (this.setState({mode: 1}))}
                        insertClick={this.handleSave}
                        onChange={(data) => (this.setState({contents: data}))}/>
            </div>
            <div>
                <div id="card-list">
                    <div className="ui cotainer">
                        <CardList contents={this.state.contents}
                                  onDelete={this.handleDelete}
                                  onChange={(data) => (this.setState({contents: data}))}/>
                    </div>
                </div>
            </div>
        </div>
    )
  },

  render : function() {
    var mode = this.state.mode;
    var content = null;
    if(mode == 1) {
        content = (
            <div>
                <button className="ui primary button"
                        onClick={() => (this.setState({mode : 0}))}>退出</button>
                <Cards contents={this.state.memoryContents} brochure={this.props.brochure}
                       onComplete={() => (this.setState({mode : 0}))}
                />
            </div>
        )

    } else if(mode == 2)
        content = this.renderCardList();
    else {
        content = (
            <div className="ui fluid card">
                <div className="center aligned content">
                    <h1>{'⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅  '+ this.props.brochure.name + '  ⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅'}</h1>
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

$(document).ready(function() {
  ReactDOM.render(<Brouchures />, document.getElementById("card"));
})
