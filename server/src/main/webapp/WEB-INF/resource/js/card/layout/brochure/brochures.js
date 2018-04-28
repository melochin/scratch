import {Brochure} from './brochure'
import {BrochureModal} from './brochureForm'
import * as brochureAction from '../../action/brochureAction'

var Brochures = React.createClass({

    getInitialState : function () {
        return {
            brochures : new Array(),
            searchOptions : new Array()
        }
    },

    componentDidMount : function () {
        this.list();
    },

    list : function () {
        brochureAction.list((data) => {
            this.setState({brochures: data.brochures, searchOptions : data.options});
        });
    },

    delete : function (brochure) {
        brochureAction.remove(brochure, () => this.list());
    },

    save : function (brochure, callback) {
        brochureAction.save(brochure, () => {
            this.list();
            message("新增成功");
            callback();
        })
    },

    modify : function (brochure, success) {
        brochureAction.modify(brochure, () => this.list());
    },

    render : function () {

        const brochures = this.state.brochures.map(brochure =>
            <Brochure key={brochure.id} brochure={brochure} delete={this.delete} modify={this.modify}/>
        );

        return (
            <div className="ui container" id="card-box" >
                <div className="ui grid">
                    {/*                  <div className="ui three wide column">
                      <Dropdown search fluid selection options={this.state.searchOptions} />
                  </div>*/}
                    <BrochureModal onSubmit={this.save} />
                </div>
                <div className="ui five stackable cards">
                    {brochures}
                </div>
            </div>
        )
    }

});

module.exports.Brochures = Brochures;
