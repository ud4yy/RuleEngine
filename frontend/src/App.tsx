import React from 'react';
import RuleEngine from './RuleEngine';
import Documentation from './Documentation';


const App: React.FC = () => {
  return (
    <div className="max-w-screen flex mx-auto p-6 bg-[#F5F5F5]">
      <div className="flex-1">
        <header className="bg-teal-600 text-white p-4 rounded-lg shadow mb-6 text-center">
          <h1 className="text-2xl font-bold">Rule Engine</h1>
          <nav>
            <ul className="flex justify-center">
              <li className="mx-4">
                <a
                  href="https://github.com/ud4yy/"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-white hover:underline"
                >
                  GitHub
                </a>
              </li>
            </ul>
          </nav>
        </header>

        <RuleEngine />
        
        <footer className="bg-teal-600 text-white text-center p-4 rounded-lg shadow mt-6">
          <p>Rule Engine powered by AST</p>
        </footer>
      </div>

      <aside className="w-1/3 ml-4">
        <Documentation />
      </aside>
    </div>
  );
};

export default App;
