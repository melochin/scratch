import {Link} from "react-router";
const containerId = "container";

const Menu = React.createClass({
    render : function () {
        const menuStyle = {padding : "0px 20px"};

        return (
            <div className="ui container" style={menuStyle}>
                <div className="ui text menu">
                    <Link className="active item" to={'/'}>首页</Link>

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
                <div id={containerId} className="ui container">
                    {this.props.children}
                </div>
            </div>
        );
    }
});

export {Layout}