export function StringSampleSolution({sample}: { sample: string }): JSX.Element {
  return (
    <div className="my-2 p-2 rounded bg-slate-200">
      <pre>{sample}</pre>
    </div>
  );
}
