interface PointsIProps {
  filledPoints: number;
  maxPoints: number;
}

export function FilledPoints({filledPoints, maxPoints}: PointsIProps): JSX.Element {
  return <>
    {Array(filledPoints).fill(0).map((_star, index) => <span key={index}>&#9899;</span>)}
    {Array(maxPoints - filledPoints).fill(0).map((_star, index) => <span key={index}>&#9898;</span>)}
  </>;
}
