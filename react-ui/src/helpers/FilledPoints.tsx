interface PointsIProps {
  filledPoints: number;
  maxPoints?: number;
  title?: string;
}

export function FilledPoints({filledPoints, maxPoints = 4, title}: PointsIProps): JSX.Element {
  return (
    <span title={title}>
      {Array(filledPoints).fill(0).map((_star, index) => <span key={index}>&#9899;</span>)}
      {Array(maxPoints - filledPoints).fill(0).map((_star, index) => <span key={index}>&#9898;</span>)}
    </span>
  );
}
