var md = require('../../../markdown').markdown;

/**
 * props : content
 *         onRember
 *         onForget
 *         onComplete
 *
 */
const Card = React.createClass({

    getInitialState : function() {
        return {front : true}
    },

    componentDidMount : function () {
        document.addEventListener("keydown", (event) => {
            if(event.code == "ArrowDown") {
                $(this.refs.btn).find("#down").click();
            }

            if(event.code == "ArrowLeft") {
                $(this.refs.btn).find("#left").click();
            }

            if(event.code == "ArrowRight") {
                $(this.refs.btn).find("#right").click();
            }

        });
    },

    // 每次接受到新属性，初始化为正面
    componentWillReceiveProps : function() {
        this.setState({front : true});
    },

    getContent : function () {
        if(this.state.front) return this.props.content['key'];
        return this.props.content['value'];
    },

    handleClick : function() {
        this.setState({front : !this.state.front});
    },

    renderButton : function () {
        var btn = null;
        // 如果是正面　button：翻转卡片
        if(this.state.front) {
            btn = <button id="down" className="ui fluid button" onClick={this.handleClick}>翻转卡片</button>
            return btn;
        }
        // 如果是反面且不是最后一张卡　button:下一张卡片
        btn =
            <div className = "ui buttons">
                <button id="left" className="ui fluid teal button" onClick={() => {
                        this.props.onRember();
                        this.props.isLast && this.props.onComplete();
                    }
                }>正确</button>
                <button id="right" className="ui fluid red button" onClick={() => {
                        this.props.onForget();
                        this.props.isLast && this.props.onComplete();
                    }
                }>错误</button>
            </div>
        return btn;
    },

    render : function() {
        const button = this.renderButton();
        const descriptionStyle = {fontSize: "2rem", padding: "6rem 0rem"};
        return(
            <div className="ui centered card" style={{width:"100%"}}>
                <div className="description center aligned twelve wide column"
                     style={descriptionStyle} onClick={this.handleClick} >
                    <div dangerouslySetInnerHTML={{__html:md.render(this.getContent())}}/>
                </div>
                <div className="extra content">
                    <div className="ui grid">
                        <div className="ui five wide computer only column"></div>
                        <div className="ui sixteen wide tablet six wide computer column " ref="btn">
                            {button}
                        </div>
                        <div className="ui five wide computer only column"></div>
                    </div>
                </div>
            </div>
        )
    }
});

export {Card}
