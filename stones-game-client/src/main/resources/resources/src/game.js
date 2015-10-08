var gameStatus = {
    "PREPARING": "warning",
    "STARTED": "success",
    "FINISHED": "info",
    "CANCELLED": "danger"
};

var GameStatus = React.createClass({

    render() {
        var winner;
        if(this.props.status == "FINISHED") {
            winner = this.props.winner;
        }
        else {
            winner = ""
        }
        return (
            <div>
                <ReactBootstrap.Alert bsStyle={gameStatus[this.props.status]} >
                    {this.props.status}<br/>
                    {winner}
                </ReactBootstrap.Alert>
            </div>
        );
    }
});

var GameBoard = React.createClass({

    move(event) {
        event.preventDefault();
        if(this.props.board.turn) {
            this.props.move(event.target.getAttribute('data-pit'));
        }
        else {
            console.log("Wrong turn");
        }
    },

    render() {

        var ownPits = [];
        this.props.board.own.pits.forEach(function(pit, index) {
            ownPits.push(<td className="success" key={index}><a href="" data-pit={index} onClick={this.move}>{pit}</a></td>);
        }, this);
        var info;
        if(this.props.board.turn) {
            info = {status:'success', message: "Its your turn, make a move"}
        }
        else {
            info = {status:'warning', message: "Its your opponent's turn, wait for a move"}
        }
        var display;
        if(this.props.display) {
            display = {display: "none"};
        }
        else {
            display ={}
        }

        return (
            <div id="game">
                <ReactBootstrap.Alert bsStyle={info.status} >
                    {info.message}<br/>
                </ReactBootstrap.Alert>
                <div className="panel panel-default">
                    <ReactBootstrap.Table bordered >
                        <tbody>
                            <tr>
                                <td className="warning" rowSpan="2" >{this.props.board.opponent.bigPit}</td>
                                <td className="warning">{this.props.board.opponent.pits[5]}</td>
                                <td className="warning">{this.props.board.opponent.pits[4]}</td>
                                <td className="warning">{this.props.board.opponent.pits[3]}</td>
                                <td className="warning">{this.props.board.opponent.pits[2]}</td>
                                <td className="warning">{this.props.board.opponent.pits[1]}</td>
                                <td className="warning">{this.props.board.opponent.pits[0]}</td>
                                <td className="success" rowSpan="2" >{this.props.board.own.bigPit}</td>
                            </tr>
                            <tr>
                                {ownPits}
                            </tr>
                        </tbody>
                    </ReactBootstrap.Table>
                </div>
                <div className="overlay img-rounded" style={display}>
                    <span>Board isnt active</span>
                </div>
            </div>
        );
    }
});



var Game = React.createClass({

    getInitialState() {
        return({status: "PREPARING",
            board: {
                own: {bigPit:0, pits:[0,0,0,0,0,0]},
                opponent: {bigPit:0, pits:[0,0,0,0,0,0]},
                turn: false,
                winner: null
            }
        });
    },

    componentWillMount() {

        this.readFeed();
        this.interval = setInterval(this.readFeed, 1000);
    },


    move(index) {

        $.ajax({
            url: "/move/" + this.props.state.gameId,
            type: "POST",
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify({userId: this.props.state.userId, pitNumber: index}),
            success: this.readFeed,
            error: function(xhr, status, err) {
                console.error(status, err.toString());
                var response =  JSON.parse(xhr.responseText);
                if(response.exception == "org.ametiste.stones.domain.GameNotFoundException") {
                    this.cancelGame();
                }
                console.error(status, err.toString());
            }.bind(this)
        });


    },



    renewState(data) {

        var turn = (data.roundOwner ==this.props.state.userId);
        var own;
        var opponent;
        if(data.firstUserBoard.userId == this.props.state.userId ) {
            own = {bigPit: data.firstUserBoard.bigPit, pits: data.firstUserBoard.pits}
            opponent = {bigPit: data.secondUserBoard.bigPit, pits: data.secondUserBoard.pits}
        }
        else {
            opponent = {bigPit: data.firstUserBoard.bigPit, pits: data.firstUserBoard.pits}
            own = {bigPit: data.secondUserBoard.bigPit, pits: data.secondUserBoard.pits}
        }

        var winnerMessage;

        if(data.winner==null) {
            winnerMessage = "Its a parity";
        }
        else {
            if(data.winner==this.props.state.userId) {
                winnerMessage = "Congratulations, you won!";
            }
            else {
                winnerMessage = "You lost. Better luck next time";
            }
        }

        this.setState({status: data.status, board: {turn: turn, own:own, opponent: opponent, winner: winnerMessage }});
    },

    readFeed() {
        $.ajax({
            url: "/feed/" + this.props.state.gameId,
            type: "GET",
            dataType: 'json',
            contentType: "application/json",
            success: this.renewState,
            error: function(xhr, status, err) {
               var response =  JSON.parse(xhr.responseText);
                if(response.exception == "org.ametiste.stones.domain.GameNotFoundException") {
                    this.cancelGame();
                }
                console.error(status, err.toString());
            }.bind(this)
        });
    },

    cancelGame() {

        $.ajax({
            url: "/cancel/" + this.props.state.gameId,
            type: "POST",
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify({userId: this.state.userId}),
            success: function() {
            },
            error: function(xhr, status, err) {
                console.log(xhr);
                console.error(status, err.toString());
            }
        });
        clearInterval(this.interval);
        this.props.cancelGame();
    },

    render() {
        return (
            <div>
                <GameStatus status={this.state.status} winner={this.state.board.winner}/>
                <GameBoard board = {this.state.board} move={this.move} display={this.state.status == "STARTED"}/>
                <ReactBootstrap.Button onClick={this.cancelGame} title="(╯°□°）╯︵ ┻━┻">Quit a game</ReactBootstrap.Button>
            </div>
        );
    }
});
