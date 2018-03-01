
var Run = React.createClass({

  getInitialState : function() {
    return {
      messages : null,
      run : false
    }
  },

  componentDidMount : function() {
    if(this.api.isRun()) {
      this.setState({run : true});
    }
  },

  componentDidUpdate : function(prevProps, prevState) {
    // 服务运行且timeout不存在，开启间隔执行
    if(this.state.run && !this.timeout) {
      this.timeout = setInterval(this.refreshMessages, 1000);
    }
    // 服务暂停 timeout存在，关闭间隔执行
    if(!this.state.run && this.timeout) {
      clearInterval(this.timeout);
      this.timeout = undefined;
    }
  },

  // 初始化状态
  api : {
    isRun : function() {
      var status = false;
      $.ajax("/api/admin/scratch/runstatus", {
        async : false,
        success : function(data) {
          status = data.status;
        }
      });
      return status;
    },
    getMessages : function() {
      var dataMessages = null;
      $.ajax("/api/admin/scratch/runmessage", {
        async : false,
        success : function(data) {
          dataMessages = data;
        }
      });
      return dataMessages;
    },
    run : function() {
      var status = false;
      $.ajax("/api/admin/scratch/run", {
        type : "get",
        async : false,
        success : function(data) {
          status = data.status;
        }
      });
      return status;
    }
  },

  handleClick : function() {
    // 若服务正在运行，则返回
    if(this.state.run) return;
    // 调用运行服务
    var isRun = this.api.run();
    // 调用失败，服务没有运行
    if(!isRun) return;
    // 运行成功，更改状态
    this.setState({run : true});
  },

  refreshMessages : function() {
    var data = this.api.getMessages();
    if(data.messages != null && data.messages.length > 0) {
      this.setState({messages : data.messages});
    }
    this.setState({run : data.status});
  },

  render : function() {
    return(
      <div className="ui grid">
        <div className="one column row">
          <div className="column">
            <label>服务状态：</label>
            <text>
                {this.state.run && "运行"}
                {!this.state.run && "停止"}
            </text>
            <button className="ui primary small button" onClick={this.handleClick}
                    disabled={this.state.run && "disabled"}>启动服务</button>
            <label></label>
          </div>
        </div>
        <div className="row">
            <div className="column">
                <TaskTime />
            </div>
        </div>
        <div className="one column row">
          <div className="column">
            <Log messages={this.state.messages}/>
          </div>
        </div>
      </div>
    )
  }

});


var TaskTime = React.createClass({

    componentDidMount : function () {
        $(this.refs.accordion).accordion();
    },

    render : function () {
        return (
            <div className="ui accordion" ref="accordion">
                <div className="title"><i className="dropdown icon"></i>定时任务</div>
                <div className="content">
                    <TaskForm/>
                </div>
            </div>
        )
    }
});


/**
 * 日志
 * 1.采用隐藏菜单风格
 * 2.日志信息更新时，自动打开隐藏的信息
 */
var Log = React.createClass({

    renderItems : function (messages) {
      if(messages == null) return null;
      return messages.map((message, index) => {
          return  <div className="item" key={index}>{message}</div>
      });
    },

    componentDidMount : function () {
        $(this.refs.accordion).accordion();
    },

    componentDidUpdate : function () {
      this.refs.list.scrollTop = this.refs.list.scrollHeight;
      $(this.refs.accordion).accordion('open', 0);
    },

    render : function () {
      return(
          <div className="ui accordion" ref="accordion">
              <div className="title"><i className="dropdown icon"></i>日志</div>
              <div className="content" ref="list" scrolling="auto"
                   style={{minHeight:"5rem",
                       maxHeight:"10rem",
                       overflow:"scroll",
                       border:"1px solid #d7cfcf",
                       padding:"5px"}}>
                  <div className="ui list">
                      {this.renderItems(this.props.messages)}
                  </div>
              </div>
          </div>
      )
    }
})


var TaskForm = React.createClass({

    getInitialState : function () {
        return({
            nextTime : null,
            startTime : "",
            interval : "",
        });
    },

    componentDidMount : function () {
      var _this = this;
      $.ajax("/api/admin/scratch/run/time", {
          success : function(data) {
            _this.setState({
                nextTime : data.nextTime,
                startTime : data.startTime,
                interval : data.interval
            });
        }
      })
    },

    handleChange : function (event) {
        if(event.target.name == "startTime") {
            this.setState({startTime : event.target.value});
        }
        if(event.target.name == "interval") {
            this.setState({interval : event.target.value});
        }
    },
    
    handleSubmit : function() {
      var _this = this;
      var time = $("input[name='startTime']").get(0).value;
      var interval = $("input[name='interval']").get(0).value;
      $.ajax("/api/admin/scratch/run/time/" + time + "/interval/" + interval,{
          success : function (data) {
              _this.setState({
                  nextTime : data.nextTime,
                  startTime : data.startTime,
                  interval : data.interval
              });
          }});
    },

    render : function() {
      return(
          <div className="ui segement" style={{width:"25rem"}}>
            <div className="ui form">
              <div className="field">
                <label>预估下次启动 : {this.state.nextTime}</label>
              </div>
              <div className="field">
                <label>启动时间</label>
                <input type="time" name="startTime"  value={this.state.startTime} onChange={this.handleChange}></input>
              </div>
              <div className="field">
                <label>间隔(单位：分)</label>
                <input type="number" name="interval" value={this.state.interval} onChange={this.handleChange}></input>
              </div>
              <a className="ui button" onClick={this.handleSubmit}>
                保存</a>
            </div>
          </div>
      );
    }
});


$(document).ready(function() {
  ReactDOM.render(<Run /> , document.getElementById("runButton"));
});



