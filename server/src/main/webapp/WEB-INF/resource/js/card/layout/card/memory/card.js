var md = require('../../../markdown').markdown;

const Card = React.createClass({

    getInitialState : function() {
        return {front : true}
    },

    componentDidMount : function () {
        document.addEventListener("keydown", (event) => {
            if(event.code == "ArrowDown") {
                $(this.refs.btn).find("button").click();
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
            btn = <button className="ui fluid button" onClick={this.handleClick}>翻转卡片</button>
            return btn;
        }
        // 如果是反面且不是最后一张卡　button:下一张卡片
        if(!this.props.isLast) {
            btn = <button className="ui fluid button" onClick={this.props.onRightClick}>下一张卡片</button>
            return btn;
        }

        // 反面且最后一张　button:完成
        btn  = <button className="ui teal fluid button" onClick={() => {
            this.props.onRightClick();
            this.props.onComplete();}}>完成</button>
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
