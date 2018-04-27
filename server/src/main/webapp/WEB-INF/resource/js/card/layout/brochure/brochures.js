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
        var _this = this;
        brochureAction.list((data) => {
            _this.setState({brochures: data.brochures, searchOptions : data.options});
        });
    },

    delete : function (brochure) {
        var _this = this;
        brochureAction.remove(brochure, () => _this.list());
    },

    save : function (brochure, callback) {
        var _this = this;
        brochureAction.save(brochure, () => {
            _this.list();
            message("新增成功");
            callback();
        })
    },

    modify : function (brochure, success) {
        var _this = this;
        Ajax.put("/api/brochures", brochure, {
            success : function () {
                _this.list();
            }
        })
    },

    renderBrouchure : function () {
        var _this = this;
        return this.state.brochures.map(brochure => {
            return(
                <Brochure key={brochure.id} brochure={brochure} delete={_this.delete} modify={_this.modify}/>
            )
        });
    },

    render : function () {
        return (
            <div className="ui container" id="card-box" >
                <div className="ui grid">
                    {/*                  <div className="ui three wide column">
                      <Dropdown search fluid selection options={this.state.searchOptions} />
                  </div>*/}
                    <BrochureModal onSubmit={this.save} />
                </div>
                <div className="ui five stackable cards">
                    {this.renderBrouchure()}
                </div>
            </div>
        )
    }

});

module.exports.Brochures = Brochures;
