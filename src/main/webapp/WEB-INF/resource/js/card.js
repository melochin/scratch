var Remarkable = require('remarkable');
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

var Card = React.createClass({

  getInitialState : function() {
    return {
      display : "key"
    }
  },

  componentWillReceiveProps : function() {
    this.setState({display : "key"});
  },

  handleClick : function() {
    if(this.state.display == "key") {
      this.setState({display : "value"});
    } else {
      this.setState({display : "key"});
    }
  },

  render : function() {
    return(
      <div className="ui centered card" style={{minWidth:"20rem"}}>
        <div className="content" onClick={this.handleClick}>
          <div className="description center aligned" style={{fontSize : "20px"}} >
          <div dangerouslySetInnerHTML={{__html:md.render(this.props.content[this.state.display])}}/>
          </div>
        </div>
        <div className="extra content">
        {!this.props.isFirst &&
          <span className="left floated ">
            <button className="ui icon primary button" onClick={this.props.onLeftClick}>
              <i className="arrow left icon"></i>
            </button>
          </span>
        }
        {!this.props.isLast &&
          <span className="right floated ">
            <button className="ui icon primary button" onClick={this.props.onRightClick}>
              <i className="arrow right icon"></i>
            </button>
          </span>
        }
        </div>
      </div>
    )
  }
});

var Cards = React.createClass({
  getInitialState : function() {
    return ({
      current : 0
    })
  },

  handleClick : function() {
    if(this.state.current < this.props.contents.length - 1) {
      this.setState({current : this.state.current + 1});
    }
  },

  setCurrent : function(current) {
    if(current < 0 || current >= this.props.contents.length) {
      return;
    }
    this.setState({current : current});
  },

  renderCard : function(index) {
    if(index >= this.props.contents.length || index < 0) return null;
    var content = this.props.contents[index];
    var isFirst = false;
    var isLast = false;
    if(index == 0) {
      isFirst = true;
    }
    if(index == this.props.contents.length - 1) {
      isLast = true;
    }

    return <Card content={content} isFirst = {isFirst} isLast = {isLast}
      onLeftClick={() => (this.setCurrent(this.state.current - 1))}
      onRightClick={()=> (this.setCurrent(this.state.current + 1))}/>
  },

  render : function() {
    return (
      <div className="ui container" style={{margin : "2rem"}}>
          {this.renderCard(this.state.current)}
      </div>
    )
  }
})

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
    var json = JSON.stringify(card);
    var _this = this;
    $.ajax("/api/cards", {
      data : json,
      method : "post",
      contentType : "application/json; charset=utf-8",
      headers : getToken(),
      success : function(data) {
        _this.props.onChange(data);
      }
    });
    this.setState({isInsert : false})
  },

  onCancelClick : function() {
    this.setState({isInsert : false})
  },

  render : function() {
    return (
    <div>
    <div className="blue ui buttons">
      <button className="ui small primary button" style={{margin : "10px 0 10px 0"}}
        onClick={this.props.memoryclick}>记忆模式</button>
      <button className="ui small primary button"style={{margin : "10px 0 10px 0"}}
        onClick={this.onInsertClick}>新增</button>
    </div>
      {this.state.isInsert &&
        <div className="ui form" style={{maxWidth: "14rem", padding : "14px", border : "1px solid #ced6d5"}}>
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
    </div>
    )
  }
})



var CardList = React.createClass({

  renderCard : function(content) {
    var _this = this;
    var handleDelete = function() {
      var json = JSON.stringify(content);
      $.ajax("/api/cards", {
        data : json,
        method : "delete",
        contentType : "application/json; charset=utf-8",
        headers : getToken(),
        success : function(data) {
          _this.props.onChange(data);
        }
      });
    }

    return (
      <div className="card-item ui grid" key={content.id}>
        <div className="fifteen wide column" style={{padding :"0 0 0 14px"}}>
          <div className="title">
            <span dangerouslySetInnerHTML={{__html:md.render(content.key)}}/>
          </div>
          <div className="content">
            <span className="transition hidden" dangerouslySetInnerHTML={{__html:md.render(content.value)}} />
          </div>
        </div>
        <div className="one wide column" style={{padding : "14px 0 14px 0"}}>
          <button className="ui mini icon button"
            style={{background : "white"}} onClick={handleDelete}>
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
      <div className="ui styled fluid accordion">
        {cards}
      </div>
    )
  },
})

        //<Cards contents={contents}/>
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
        memoryMode : false
      }
    )
  },

  // 加载数据
  componentDidMount : function() {
    var _this = this;
    var last_response_len = false;
    var last_response_data;
    var i = 0;

    var source=new EventSource("/api/stream/cards");
    source.onmessage=function(event)
    {
      console.debug("event source");
      if(event.data != last_response_data) {
        console.debug("refresh");
        _this.setState({contents : JSON.parse(event.data)})
        last_response_data = event.data;
      }
    };

/*
    $.ajax("/api/cards", {
      method : "get",
      contentType : "application/json; charset=utf-8",
      headers : getToken(),
      success : function(data) {
        //
      },
      xhrFields: {
                onprogress: function(e)
                {
                    var this_response, response = e.currentTarget.response;
                    console.log(e);
                    console.log(i, response);

                    i++;
                    if(last_response_len === false)
                    {
                        this_response = response;
                        last_response_len = response.length;
                    }
                    else
                    {
                        this_response = response.substring(last_response_len);
                        last_response_len = response.length;
                    }
                    try{
                      console.debug("{" + this_response +"}");
                      var data = JSON.parse("{" + this_response +"}");
                      if(!data.hasOwnProperty("data")) return;
                      console.debug(data);
                    } catch(error) {
                      console.debug(this_response);
                      console.debug(error);
                    }
                }
            }
    });*/
  },

  render : function() {
    var memoryMode = this.state.memoryMode;
    if(memoryMode) {
      return (
        <div id="card-box" className="ui grid">
          <div>
            <button className="ui primary button"
              onClick={() => (this.setState({memoryMode : false}))}>退出</button>
          </div>
          <Cards contents={this.state.contents}/>
        </div>
      )
    } else {
      return (
        <div id="card-box" className="ui grid">
          <div className="four wide column">
            <Button memoryclick={() => (this.setState({memoryMode : true}))}
              onChange={(data) => (this.setState({contents : data}))}/>
          </div>
          <div className="twelve wide column">
            <div id="card-list">
              <div className="ui cotainer">
                <CardList contents={this.state.contents}
                  onChange={(data) => (this.setState({contents:data}))}/>
              </div>
            </div>
          </div>
        </div>
      )
    }
  }
})

$(document).ready(function() {
  ReactDOM.render(<Box />, document.getElementById("card"));
})
