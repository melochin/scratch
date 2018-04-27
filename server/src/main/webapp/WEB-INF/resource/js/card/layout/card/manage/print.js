import * as cardAction from '../../../action/cardAction';
var md = require('../../../markdown').markdown;

const Print = React.createClass({

    getInitialState : function () {
        return {cards : new Array};
    },

    componentDidMount : function () {
        const brochureId = this.props.params.brochureId;
        cardAction.list(brochureId, (data) => this.setState({cards : data}));
    },

    render : function () {

        const keys = this.state.cards.map((card, index) =>
            <div key={index} className="ui four wide column"  style={{marginBottom : "20px"}}>
                <p dangerouslySetInnerHTML={{__html:md.render(card.key)}}></p>
            </div>
        );

        const values = this.state.cards.map((card, index) =>
            <span key={index} style={{marginRight : "32px"}} dangerouslySetInnerHTML={{__html:md.render(card.value)}}></span>
        );

        return (
            <div className="ui container" style={{fontSize: "16px"}}>
                <div className="ui grid" style={{margin: "20px 10px 20px 10px"}}>
                    {keys}
                </div>
                <div style={{margin: "20px 24px 20px 24px"}}>
                    <div style={{marginBottom: "16px"}}>
                        <label>答案:</label>
                    </div>
                    {values}
                </div>
            </div>
        )
    }
})

export {Print}