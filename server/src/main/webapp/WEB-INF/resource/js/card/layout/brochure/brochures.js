import {Brochure} from './brochure'
import {BrochureModal} from './brochureForm'
import * as brochureAction from '../../action/brochureAction'
import brochureStore from "./brochureStore"

var Brochures = React.createClass({

    getInitialState : function () {
        return {
            brochures : new Array(),
            searchOptions : new Array()
        }
    },

    componentWillMount : function () {
        brochureStore.on("change", () => {
            console.log("re-render");
            this.setState({brochures : brochureStore.list()});
        })
    },

    componentDidMount : function () {
        brochureAction.list();
    },

    render : function () {

        const brochures = this.state.brochures.map(brochure =>
            <Brochure key={brochure.id} brochure={brochure} />
        );

        return (
            <div className="ui container" id="card-box" >
                <div className="ui grid">
                    {/*                  <div className="ui three wide column">
                      <Dropdown search fluid selection options={this.state.searchOptions} />
                  </div>*/}
                    <BrochureModal  />
                </div>
                <div className="ui five stackable cards">
                    {brochures}
                </div>
            </div>
        )
    }

});

module.exports.Brochures = Brochures;
