import js from '@eslint/js';
import { defineConfig } from 'eslint/config';
import pluginImport from 'eslint-plugin-import';
import pluginVue from 'eslint-plugin-vue';
import globals from 'globals';

export default defineConfig([
  // JS de base
  {
    files: ['**/*.{js,mjs,cjs,vue}'],
    languageOptions: {
      globals: globals.browser,
      ecmaVersion: 2020,
      sourceType: 'module'
    },
    plugins: {
      js,
      import: pluginImport
    },
    rules: {
      ...js.configs.recommended.rules,

      // Règles import importantes
      'import/no-unresolved': 'error',
      'import/named': 'error',
      'import/no-extraneous-dependencies': 'warn',
      'import/order': [
        'warn',
        {
          groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index'],
          alphabetize: { order: 'asc', caseInsensitive: true }
        }
      ]
    },
    settings: {
      'import/resolver': {
        alias: {
          map: [['@', './src']],
          extensions: ['.js', '.vue']
        }
      }
    }
  },

  // Vue plugin (essential = minimum recommandé)
  pluginVue.configs['flat/essential']
]);
