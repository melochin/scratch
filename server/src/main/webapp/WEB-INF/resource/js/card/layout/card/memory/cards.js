import {Progress} from './progress'
import {Card} from './card'

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
        var _this = this;
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
                     onComplete = {this.props.onComplete}
                     onLeftClick={() => (this.setCurrent(this.state.current - 1))}
                     onRightClick={()=> {
                         Ajax.put("/api/brochures/" + _this.props.brochure.id + "/cards/memory", content, {
                             success : function () {
                                 _this.setCurrent(_this.state.current + 1)
                             }
                         });
                     }}/>
    },

    render : function() {
        return (
            <div className="ui container" style={{margin : "2rem"}}>
                {this.renderCard(this.state.current)}
                <Progress current={this.state.current} total={this.props.contents.length} />
            </div>
        )
    }
})

module.exports.Cards = Cards;