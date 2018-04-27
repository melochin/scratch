import {Link} from "react-router";
const containerId = "container";

const Menu = React.createClass({
    render : function () {
        return (
            <div className="ui container" style={{padding: "0px 20px"}}>
                <div className="ui text menu">
                    <Link  className="active item" to={'/'}>首页</Link>
                </div>
            </div>
        )
    }

})

const Layout = React.createClass({

    render : function () {
        return (
            <div>
                <Menu/>
                <div id={containerId}>
                    {this.props.children}
                </div>
            </div>
        );
    }
});

module.exports.Layout = Layout;