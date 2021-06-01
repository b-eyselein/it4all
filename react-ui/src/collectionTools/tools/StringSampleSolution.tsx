import React from "react";

export function StringSampleSolution({sample}: { sample: string }): JSX.Element {
  return (
    <div className="notification is-light-grey">
      <pre>{sample}</pre>
    </div>
  );
}
