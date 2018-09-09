import {EventEmitter} from "events"
import dispatcher from './dispatcher';

class BrochureStore extends EventEmitter {

    constructor() {
        super();
        this.brochures = new Array();
    }

    set(brochures) {
        console.log("store change");
        this.brochures = brochures;
        this.emit("change");
    }

    list() {
        return this.brochures;
    }

    handle(action) {
        console.log("handle dispatch");
        switch (action.type) {
            case 'list' :
                this.brochures = action.brochures;
                this.emit("change");
                break;
            case 'save' :
                this.brochures.push(action.brochure);
                this.emit("change");
                break;
            case 'modify' :
                this.brochures = this.brochures.map(brochure => {
                    if(brochure.id == action.brochure.id) {
                        return action.brochure;
                    }
                    return brochure;
                });
                this.emit("change");
                break;
            case 'delete' :
                this.brochures = this.brochures.filter((brochure) => brochure.id != action.brochure.id);
                this.emit("change");
                break;
        }
    }
}


const brochureStroe = new BrochureStore;

dispatcher.register(brochureStroe.handle.bind(brochureStroe));


export default brochureStroe;
