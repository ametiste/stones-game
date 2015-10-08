var Login = React.createClass({
    render() {
        return (
            <div id="loginForm">
                <ReactBootstrap.Button onClick={this.props.joinGame}>Join a game</ReactBootstrap.Button>
            </div>
        );
    }
});

var StonesApp = React.createClass({

    getInitialState() {
        return {gameId: null, userId:null};
    },

    componentWillMount() {
        this.loadUser();
        this.loadGame();
    },


    loadUser()  {
        var userId = localStorage.getItem("userId");
        if(userId!=null) {
            this.setState({userId: userId});
        }
    },

    loadGame()  {
        //need to be more complicated check
        //var gameId = localStorage.getItem("userId");
        //if( gameId!=null) {
        //    this.setState({gameId: gameId});
        //}
    },

    join () {

        if(this.state.userId==null) {
            this.login(function(data) {
                this.setState({"userId": data.userId});
                localStorage.setItem("userId", data.userId);
                this.sendJoin();
            }.bind(this));
        }
        else {
            this.sendJoin();
        }
    },


    sendJoin() {
        $.ajax({
            url: "/join",
            type: "POST",
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify({userId: this.state.userId}),
            success: function(data) {
                this.setState({"gameId": data.gameId});
                localStorage.setItem("gameId", data.gameId);
            }.bind(this),
            error: function(xhr, status, err) {
                console.log(xhr);
                console.error(status, err.toString());
            }
        });
    },

    login(callback) {
        $.ajax({
            url: "/login",
            type: "POST",
            dataType: 'json',
            contentType: "application/json",
            success: callback,
            error: function(xhr, status, err) {
                console.log(xhr);
                console.error(status, err.toString());
            }
        });

    },

    cancel() {
        this.setState({gameId:null});
    },

    userJoined() {
      return this.state.gameId!=null;
    },

    render() {
        var component;
        if(this.userJoined()) {
            component = <Game state={this.state} cancelGame={this.cancel}/>
        } else {
            component = <Login joinGame={this.join}/>
        }

        return (
            <div className="container">
                {component}
            </div>
        );
    }

});

React.render(
    <StonesApp uri="http://localhost:8080/"/>, document.body
);