import * as joint from 'jointjs';

import * as _ from 'lodash';

declare module 'jointjs' {
  namespace shapes {
    namespace logic {
      interface Gate {
        signal: number;

        onSignal(signal: boolean, handler: (b: boolean) => void): void;
      }
    }
  }
}

joint.shapes.logic.Gate.prototype.onSignal = (signal: boolean, handler: (b: boolean) => void): void => {
  handler.call(this, signal);
};

// The repeater delays a signal handling by 400ms
joint.shapes.logic.Repeater.prototype.onSignal = (signal: boolean, handler: (b: boolean) => void): void => {
  _.delay(handler, 400, signal);
};

// Output element just marks itself as alive.
joint.shapes.logic.Output.prototype.onSignal = (signal: boolean, handler: (b: boolean) => void): void => {
  // FIXME: toggleLive(this, signal);
};
