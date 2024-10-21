import React from 'react';
import RuleEngine from './RuleEngine';

const Documentation: React.FC = () => {
  return (
    <div className="p-4 bg-[#F5F5F5] h-full overflow-y-auto rounded-lg shadow">
      <h2 className="text-xl font-semibold text-teal-600 mb-4">How to Use the Rule Engine</h2>
      <p className="mb-2">1. **Create a Rule**: Enter your rule in the text area and click "Create Rule".</p>
      <p className="mb-2">2. **Evaluate a Rule**: Select an existing rule from the dropdown and provide JSON input.</p>
      <p className="mb-2">3. **Combine Rules**: Select multiple rules and click "Combine Rules" to create a new rule string.</p>
      <p className="mb-2">4. **Results**: The evaluation result will be displayed below.</p>
      <p className="mt-4 text-sm text-gray-600">For more details, check the documentation.</p>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <div className="max-w-screen flex mx-auto p-6 bg-[#F5F5F5]">
      <div className="flex-1">
        <header className="bg-teal-600 text-white p-4 rounded-lg shadow mb-6 text-center">
          <h1 className="text-2xl font-bold">Rule Engine UI</h1>
          <nav>
            <ul className="flex justify-center">
              <li className="mx-4">
                <a
                  href="https://github.com/yourusername/yourrepository"
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
