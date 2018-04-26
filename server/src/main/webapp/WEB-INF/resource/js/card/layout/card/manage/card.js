var ClipboardJS = require('clipboard');
var md = require('../../../markdown').markdown;

/**
 * props : no
 *         card
 *         onDelete
 * @type {*|Function}
 */
const Card = React.createClass({

    componentDidMount : function () {
        var _this = this;
        new ClipboardJS(this.refs.key, {
            text : function (trigger) {
                return _this.props.card.key
            }
        });

        new ClipboardJS(this.refs.value, {
            text : function (trigger) {
                return _this.props.card.value
            }
        });

    },

    handleDrop : function (event) {
        var id = event.dataTransfer.getData("Text");
        if(id == null) return;
        console.log(id);
        //console.log(this.props.card); ???怎么可以放到　目标元素的props?
        this.props.onSwap(this.props.card.id, id);
    },

    render : function () {
        return (
            <div className="item" ref="card" key={this.props.card.id}
                 draggable={true} onDragOver={event => event.preventDefault()}
                 onDragStart={(event) => event.dataTransfer.setData("Text", this.props.card.id)}
                 onDragEnter={(event) => this.refs.card.style.background = "#bbeaf3" }
                 onDragLeave={(event) => this.refs.card.style.background = ""}
                 onDrop={(event) => this.handleDrop(event)}>
                {/*<div className="content">{this.props.no}</div>*/}
                <div className="content">
                    <div className="description">
                        <span dangerouslySetInnerHTML={{__html:md.render(this.props.card.key)}} ref="key"/>
                    </div>
                </div>
                <div className="content">
                    <div className="description">
                        <span dangerouslySetInnerHTML={{__html:md.render(this.props.card.value)}} ref="value"/>
                    </div>
                    <button className="ui right floated icon button"
                            style={{background : "white"}} onClick={() => this.props.onDelete(this.props.card, null)}>
                        <i className="trash outline icon"></i>
                    </button>
                </div>
            </div>
        )
    }
})

module.exports.Card = Card;