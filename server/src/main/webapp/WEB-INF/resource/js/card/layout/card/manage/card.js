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
        new ClipboardJS(this.refs.key, {
            text : (trigger) => this.props.card.key
        });

        new ClipboardJS(this.refs.value, {
            text : (trigger) => this.props.card.value
        });
    },

    handleDrop : function (event) {
        var id = event.dataTransfer.getData("Text");
        if(id == null) return;
        this.props.onSwap(id, this.props.card.id);
    },

    render : function () {

        const dragEnterStyle = {background : "bbeaf3"};
        const dragLeaveStyle = {background: ""};
        const iconStyle = {background : "white"};
        const card = this.props.card;
        const remberCount = card.remeber + card.forget;
        const percent = remberCount == 0 ? 0 :
            (card.remeber / remberCount).toFixed(2) * 100;

        return (
            <tr className="item" ref="card" key={this.props.card.id}
                 draggable={true} onDragOver={event => event.preventDefault()}
                 onDragStart={(event) => event.dataTransfer.setData("Text", this.props.card.id)}
                 onDragEnter={(event) => this.refs.card.style = dragEnterStyle }
                 onDragLeave={(event) => this.refs.card.style = dragLeaveStyle}
                 onDrop={(event) => {
                     this.handleDrop(event)
                     this.refs.card.style = dragLeaveStyle;
                 }}>
                <td className="disabled">
                    {this.props.no}
                </td>
                <td>
                    <span dangerouslySetInnerHTML={{__html:md.render(card.key)}} ref="key"/>
                </td>
                <td>
                    <span dangerouslySetInnerHTML={{__html:md.render(card.value)}} ref="value"/>
                </td>
                <td>
                    <span>{percent}% ({remberCount}) </span>
                </td>
                <td>
                    <button className="ui right floated icon button"
                            style={iconStyle} onClick={() => this.props.onDelete(this.props.card, null)}>
                        <i className="trash outline icon"></i>
                    </button>
                </td>
            </tr>
        )
    }
})

module.exports.Card = Card;