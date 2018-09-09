import G2 from '@antv/g2';

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

    : : function (callback) {
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

var RunInfo = React.createClass({

    getInitialState : function() {
        return {
            runInfo : null
        }
    },

    componentDidMount : function() {
        RunAction.info(
            (data) => this.setState({runInfo : data})
        );
        setInterval(this.refreshMessages, 5000);
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

    render : function() {
        var runInfo = this.state.runInfo;
        var run = runInfo != null ? runInfo.run : false;
        var timeRun = runInfo != null ? runInfo.timeRun : false;
        var nextTime = runInfo != null ? runInfo.nextTime : "";
        var startTime = runInfo != null ? runInfo.startTime : "";

        var getCircleIconClass = (status) => (status ? "green cirecle icon" : "black circle icon");
        var getBtnIconClass = (status) => (status ? "pause icon" : "play icon");
        var iconStyle = {paddingLeft: "0.5rem"};

        return (
            <div className="ui row">
                <div className="ui left labeled button" tabindex="0">
                      <span className="ui large basic label">
                          抓取服务
                          <i className={getCircleIconClass(run)} style={iconStyle}/>
                      </span>
                    <button className="ui icon button" onClick={this.handleRunClick}
                            disabled={run ? "disabled" : ""}>
                        <i className={getBtnIconClass(run)} />
                    </button>
                </div>
                <div className="ui left labeled button" tabindex="0" style={{paddingLeft:"1rem"}}>
                      <span className="ui large basic label">
                          定时服务
                          {timeRun && " (下次运行时间：" + nextTime + ")"}

                          <i className={getCircleIconClass(timeRun)} style={iconStyle}/>
                      </span>
                    <button className="ui icon button" onClick={this.handleTimeRunClick}>
                        <i className={getBtnIconClass(timeRun)} />
                    </button>
                </div>
                <div className="ui modal" ref="modal">
                    <div className="ui content">
                        <TaskForm handleSave={this.handleTimeRunSaveClick}></TaskForm>
                    </div>
                </div>
            </div>
        )
    }
});

var LogInfo = React.createClass({
    getInitialState : function() {
        return {
            logs : new Array()
        }
    },

    componentDidMount : function() {
        RunAction.logs(
            (data) => this.setState({logs : data})
        );
    },

    render : function() {
        return (
            <div className="ui column">
                <h3 className="ui top attached header">
                    日志
                </h3>
                <div className="ui attached segment" style={{font: "1.5rem"}} >
                    <SideLog logs={this.state.logs} readLog={RunAction.readLog}/>
                </div>
            </div>
        )
    }
})



var Run = React.createClass({

    render : function() {
        return(
            <div className="ui grid">
                <div className="two column row">
                    <div className="column">
                        <RunInfo/>
                    </div>
                </div>
                <div className="one column  row">
                    <div className="column">
                        <div id="chart" className="ui segment"></div>
                    </div>
                </div>
                <div className="one column row">
                    <LogInfo/>
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
                    <div className="ui three wide column">
                        <h4>文件</h4>
                    </div>
                    <div className="ui thirteen wide column">
                        <h4>内容</h4>
                    </div>
                </div>
                <div className="ui row">
                    <div className="ui three wide column" style={style}>
                        <div className="ui list">{names}</div>
                    </div>
                    <div className="ui thirteen wide column" style={style}>
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
  Ajax.get("/api/admin/scratch/run/records/date", null,{
        success : (data) => {

            var data = data.map(d => {
                var record = new Object();
                record.date = new Date(parseInt(d.date)).toLocaleDateString();
                record.times = d.times;
                return record;
            });

            const chart = new G2.Chart({
                container: 'chart',
                forceFit: true
            });
// Step 2: 载入数据源
            chart.source(data);
// Step 3：创建图形语法，绘制柱状图，由 genre 和 sold 两个属性决定图形位置，genre 映射至 x 轴，sold 映射至 y 轴
            chart.interval().position('date*times').color('genre')
// Step 4: 渲染图表
            chart.render();
        }
      })
});
