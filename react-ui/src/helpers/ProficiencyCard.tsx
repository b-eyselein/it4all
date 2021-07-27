import React from 'react';
import {UserProficiencyFragment} from '../graphql';
import {FilledPoints} from './FilledPoints';

interface IProps {
  proficiency: UserProficiencyFragment;
}

export function ProficiencyCard({proficiency: {topic, level, points, pointsForNextLevel}}: IProps): JSX.Element {

  return (
    <div className="card">
      <header className="card-header">
        <p className="card-header-title">{topic.title}</p>
      </header>
      <div className="card-content">
        <p>Level: {level.title}</p>

        <p>{points} von {pointsForNextLevel} Punkten für nächstes Level</p>

        <p>
          <FilledPoints filledPoints={level.levelIndex} maxPoints={topic.maxLevel.levelIndex}/>
        </p>


        {/*
        <hr>

        <div className="table-container">

          <table className="table is-fullwidth">
            <thead>
              <tr>
                <th>Level</th><th>Punkte</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Beginner</td><td>{{proficiency.beginnerPoints}}</td>
              </tr>
              <tr>
                <td>Intermediate</td><td>{{proficiency.intermediatePoints}}</td>
              </tr>
              <tr>
                <td>Advanced</td><td>{{proficiency.advancedPoints}}</td>
              </tr>
              <tr>
                <td>Expert</td><td>{{proficiency.expertPoints}}</td>
              </tr>
            </tbody>
          </table>

        </div>
       */}

      </div>
    </div>
  );

}
