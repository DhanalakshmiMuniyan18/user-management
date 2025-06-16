"""
Unit tests for DatabaseConnectionFactory (Python version).
Covers valid, invalid, null, empty, and whitespace-only input cases.
"""
import pytest
from typing import Any

class DatabaseConnectionFactory:
    @staticmethod
    def get_connection(db_type: str) -> Any:
        if not db_type or not db_type.strip():
            raise ValueError("Unknown database type: {}".format(db_type))
        db_type = db_type.strip()
        if db_type == "MySQL":
            return MySQLConnection()
        elif db_type == "PostgreSQL":
            return PostgreSQLConnection()
        elif db_type == "Oracle":
            return OracleConnection()
        else:
            raise ValueError(f"Unknown database type: {db_type}")

class MySQLConnection:
    pass
class PostgreSQLConnection:
    pass
class OracleConnection:
    pass

def test_valid_connections():
    cases = [
        ("MySQL", MySQLConnection),
        ("PostgreSQL", PostgreSQLConnection),
        ("Oracle", OracleConnection)
    ]
    for db_type, expected_class in cases:
        conn = DatabaseConnectionFactory.get_connection(db_type)
        assert isinstance(conn, expected_class), f"Expected {expected_class.__name__} for type {db_type}"

def test_unknown_connection():
    with pytest.raises(ValueError) as exc:
        DatabaseConnectionFactory.get_connection("SQLite")
    assert "Unknown database type" in str(exc.value)

import pytest
import itertools
@pytest.mark.parametrize("db_type", [None, "", " ", "   ", "\t", "\n"])
def test_null_empty_whitespace_connection_type(db_type):
    with pytest.raises(ValueError) as exc:
        DatabaseConnectionFactory.get_connection(db_type)
    assert "Unknown database type" in str(exc.value)

