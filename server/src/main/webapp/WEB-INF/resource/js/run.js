import G2 from '@antv/g2';

const data = [
    { genre: 'Sports', sold: 275 },
    { genre: 'Strategy', sold: 115 },
    { genre: 'Action', sold: 120 },
    { genre: 'Shooter', sold: 350 },
    { genre: 'Other', sold: 150 }
]; // G2 对数据源格式的要求，仅仅是 JSON 数组，数组的每个元素是一个标准 JSON 对象。

var RunAction = {

    run : function (callback) {
        Ajax.get("/api/admin/scratch/run", null, {
            success : data => callback(data)
        });
    },

    timeRun : function (time, interval, callback) {
        Ajax.get("/api/admin/scratch/run/time/" + time + "/interval/" + interval, null, {
            success: data => callback(data)
        });
    },

    shutdownTimeRun : function (callback) {
        Ajax.get("/api/admin/scratch/run/time/shutdown", null, {
            success : data => callback(data)
        })
    },

    info : function (callback) {
        Ajax.get("/api/admin/scratch/run/message", null, {
            success : (data) => callback(data)
        })
    },

    logs : function (callback) {
        Ajax.get("/api/admin/scratch/run/logs", null, {
            success : (data) => callback(data)
        })
    },

    readLog : function (name, callback) {
        Ajax.get("/api/admin/scratch/run/logs/file?name=" + name, null, {
          success : (data) => callback(data)
        })
    }


}


var Run = React.createClass({

  getInitialState : function() {
    return {
      messages : null,
        //TODO delete run
      run : false,
      runInfo : null,
      logs : new Array()
    }
  },

  componentDidMount : function() {
      RunAction.info(
          (data) => this.setState({runInfo : data})
      );
      RunAction.logs(
          (data) => this.setState({logs : data})
      );
      setInterval(this.refreshMessages, 5000);


      $('.tabular.menu .item').tab();
  },

  componentDidUpdate : function(prevProps, prevState) {
    // 服务运行且timeout不存在，开启间隔执行
/*    if(this.state.run && !this.timeout) {
      this.timeout = setInterval(this.refreshMessages, 1000);
    }
    // 服务暂停 timeout存在，关闭间隔执行
    if(!this.state.run && this.timeout) {
      clearInterval(this.timeout);
      this.timeout = undefined;
    }*/
  },


  handleRunClick : function() {
    // 若服务正在运行，则返回
    if(this.state.runInfo != null && this.state.runInfo.run) return;
    // 更新状态
    RunAction.run((data) =>
        RunAction.info((data) => this.setState({runInfo : data}))
    );
  },

  handleTimeRunClick : function () {
    // 若服务启动中，则关闭服务器
    if(this.state.runInfo != null && this.state.runInfo.timeRun) {
        RunAction.shutdownTimeRun((data) =>
            RunAction.info((data) => this.setState({runInfo: data}))
        );
    } else {
        //TODO 显示modal
        $(this.refs.modal).modal("show");
    }
  },

  handleTimeRunSaveClick : function (time, interval) {
      RunAction.timeRun(time, interval, (data) => {
          RunAction.info((data) => this.setState({runInfo : data}) );
          $(this.refs.modal).modal("hide");
      });
  },

  refreshMessages : function() {
      RunAction.info((data) => this.setState({runInfo : data}));

  },

  renderRunTab : function () {

      var runInfo = this.state.runInfo;
      var run = runInfo != null ? runInfo.run : false;
      var timeRun = runInfo != null ? runInfo.timeRun : false;
      var nextTime = runInfo != null ? runInfo.nextTime : "";
      var startTime = runInfo != null ? runInfo.startTime : "";
      var interval = runInfo != null ? startTime.startTime : "";
      return (
          <div className={"ui bottom attached active tab segment " }  style={{font: "1.5rem"}} data-tab="run">
              <div className="ui grid">
                  <div className="ui row">
                      <div className="ui eight wide column">
                          <label>抓取服务：</label>
                          {run &&
                            <label className="ui blue label" >运行</label>
                          }
                          { !run &&
                              <label className="ui grey label" >停止</label>
                          }
                      </div>
                      <div className="ui eight wide column">
                          <button className="ui primary button" onClick={this.handleRunClick}
                                  disabled={run ? "disabled" : ""}>
                              {run == false ? "启动" : "关闭"}
                          </button>
                      </div>
                  </div>
                  <div className="ui row">
                      <div className="ui eight wide column">
                          <label>定时服务：</label>
                          <text>
                              {timeRun &&
                              <label className="ui teal label" >开启{" (下次运行时间：" + nextTime + ")"}</label>
                              }
                              { !timeRun &&
                              <label className="ui grey label" >关闭</label>
                              }
                          </text>
                      </div>
                      <div className="ui eight wide column">
                          <button className="ui primary button" onClick={this.handleTimeRunClick}>
                              {timeRun == false ? "启动" : "关闭"}
                          </button>
                      </div>
                  </div>
              </div>

              <div className="ui modal" ref="modal">
                  <div className="ui content">
                      <TaskForm handleSave={this.handleTimeRunSaveClick}></TaskForm>
                  </div>
              </div>

          </div>
      )

  },

  //TODO record tab
  renderRecordTab : function () {
      return (
          <div className="ui bottom attached tab segment" style={{font: "1.5rem"}} data-tab="record">
          </div>
      )

  },

  //TODO log tab
  renderLogTab : function () {

      var logs = ["scratch"];
      var items = logs.map(log => <a className="active item">log</a> );

      return (
          <div className="ui bottom attached tab segment" style={{font: "1.5rem"}} data-tab="log">
            <SideLog logs={this.state.logs} readLog={RunAction.readLog}/>
          </div>
      )

  },

  render : function() {

    var runInfoTab = this.renderRunTab();
    var recordTab = this.renderRecordTab();
    var logTab = this.renderLogTab();
      return(
        <div>
          <div className="ui top attached tabular menu">
              <div className="active item" data-tab="run">服务状态</div>
              <div className="item" data-tab="record">运行记录</div>
              <div className="item" data-tab="log">日志</div>
          </div>
            {runInfoTab}
            {recordTab}
            {logTab}
{/*        <div className="three column row" style={{padding: "14px"}}>
          <div className="column">
            <label>服务状态：</label>
            <text>
                {this.state.run && "运行"}
                {!this.state.run && "停止"}
            </text>
          </div>
          <div className="column"></div>
          <div className="column">
              <button className="ui primary small button" onClick={this.handleClick}
                        disabled={this.state.run && "disabled"}>启动服务</button>
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
        </div>*/}
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
 * props
 *
 * logs
 * readLog (name, callback)
 *
 * state
 *
 * active
 * content
 *
 *
 *
 */
var SideLog = React.createClass({

    getInitialState : function () {
        return ({
            active : null,
            records : new Array(),
        })
    },

    handleClick : function (name) {
      this.setState({active : name});
      this.props.readLog(name, (data) => {
          var records = data.map((item) => {
            var strs = item.split("||");
            var record = new Object();
            if(strs.length != 3) {
                record.message = strs[0].trim();
                record.time = "";
            } else {
                record.level = strs[0].trim();
                record.time = strs[1].trim();
                record.message = strs[2].trim();
            }
            return record;
          });
          this.setState({records : records});
      });
    },

    render : function() {

        const style = {height:"25rem", overflow:"scroll"};

        var content = "";
        var names = this.props.logs.map(name =>
          <div className={name == this.state.active ?  "ui active item" : "ui item"}
                onClick={() => this.handleClick(name)}>{name}</div>);
        if(this.state.records != null) {
          content = this.state.records.map(record => {

            var label = "";
            if(record.level == "INFO") {
              label = <span className="ui grey label">信息</span>
            } else if(record.level == "ERROR") {
                label = <span className="ui red label">错误</span>
            }


            return (
                <div className="ui item">
                    {label}
                    <text style={{color : "grey"}}>
                        {" " + record.time + " "}
                    </text>

                    {record.message}
                </div>)
          });
        }

        return(
            <div className="ui grid">
                <div className="ui row">
                    <div className="ui two wide column">
                        <h4>日志文件</h4>
                    </div>
                    <div className="ui fourteen wide column">
                        <h4>内容</h4>
                    </div>
                </div>
                <div className="ui row">
                    <div className="ui two wide column" style={style}>
                        <div className="ui list">{names}</div>
                    </div>
                    <div className="ui fourteen wide column" style={style}>
                        <div className="ui list">
                          {content}
                        </div>
                    </div>
                </div>
            </div>
        )

    }

})

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

/**
 * TaskForm
 * props:
 *  handleSave
 *
 */

var TaskForm = React.createClass({

    getInitialState : function () {
        return({
            startTime : "",
            interval : ""
        });
    },

    handleChange : function (event) {
        if(event.target.name == "startTime") {
            this.setState({startTime : event.target.value});
        }
        if(event.target.name == "interval") {
            this.setState({interval : event.target.value});
        }
    },

    handleSave : function() {
      var time = this.state.startTime;
      var interval = this.state.interval;
      this.props.handleSave(time, interval);
    },

    render : function() {
      return(
          <div className="ui segement">
            <div className="ui form">
              <div className="field">
                <label>启动时间</label>
                <input type="time" name="startTime"  value={this.state.startTime} onChange={this.handleChange}></input>
              </div>
              <div className="field">
                <label>间隔(单位：分)</label>
                <input type="number" name="interval" value={this.state.interval} onChange={this.handleChange}></input>
              </div>
              <a className="ui button" onClick={this.handleSave}>
                更改</a>
            </div>
          </div>
      );
    }
});


$(document).ready(function() {
  ReactDOM.render(<Run /> , document.getElementById("runButton"));
    // Step 1: 创建 Chart 对象
    const chart = new G2.Chart({
        container:  document.getElementById("timeForm"), // 指定图表容器 ID
        width : 600, // 指定图表宽度
        height : 300 // 指定图表高度
    });
// Step 2: 载入数据源
    chart.source(data);
// Step 3：创建图形语法，绘制柱状图，由 genre 和 sold 两个属性决定图形位置，genre 映射至 x 轴，sold 映射至 y 轴
    chart.interval().position('genre*sold').color('genre')
// Step 4: 渲染图表
    chart.render();
});
