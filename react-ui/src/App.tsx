import {Route, Routes} from 'react-router-dom';
import {Home} from './Home';
import {LoginForm} from './LoginForm';
import {RegisterForm} from './RegisterForm';
import {ToolBase} from './ToolBase';
import {homeUrl, loginUrl, randomToolsUrlFragment, registerUrl, toolsUrlFragment} from './urls';
import {ClaimLti} from './ClaimLti';
import {NavBar} from './NavBar';
import {BoolFillOut} from './randomTools/BoolFillOut';
import {BoolCreate} from './randomTools/BoolCreate';
import {NaryAddition} from './randomTools/NaryAddition';
import {NaryConversion} from './randomTools/NaryConversion';
import {NaryTwoConversion} from './randomTools/NaryTwoConversion';
import {RandomToolOverview} from './randomTools/RandomToolOverview';
import {boolRandomTool, naryRandomTool} from './randomTools/randomTools';

export function App(): JSX.Element {

  return (
    <>
      <NavBar/>

      <Routes>
        <Route path={homeUrl} element={<Home/>}/>

        <Route path={loginUrl} element={<LoginForm/>}/>

        <Route path={registerUrl} element={<RegisterForm/>}/>

        <Route path={`/${toolsUrlFragment}/:toolId/*`} element={<ToolBase/>}/>

        <Route path={`/${randomToolsUrlFragment}/*`}>
          <Route path={'bool'}>
            <Route index element={<RandomToolOverview tool={boolRandomTool}/>}/>
            <Route path={'fillOut'} element={<BoolFillOut/>}/>
            <Route path={'create'} element={<BoolCreate/>}/>
          </Route>
          <Route path={'nary'}>
            <Route index element={<RandomToolOverview tool={naryRandomTool}/>}/>
            <Route path={'addition'} element={<NaryAddition/>}/>
            <Route path={'conversion'} element={<NaryConversion/>}/>
            <Route path={'twoConversion'} element={<NaryTwoConversion/>}/>
          </Route>
        </Route>

        <Route path={'/lti/:ltiUuid'} element={<ClaimLti/>}/>
      </Routes>
    </>
  );
}

