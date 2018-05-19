"""This module provides the login classes for users and rooms."""

from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from passlib.hash import sha512_crypt
from .base import Base

def make_password(password: str) -> str:
    """Returns the hashed password.
    
    :param password: The password to encrypt
    :type password: str
    :return: The encrypted password
    :rtype: str
    """

    return sha512_crypt.hash(password)

class UserLogin(Base):
    """Represents the login info of an user."""

    __tablename__ = "users_login"

    userID = Column(Integer, ForeignKey("users.userID"), primary_key=True)
    password = Column(String, nullable=False)

    user = relationship("User")

    def check_password(self, pwd_to_check: str) -> bool:
        """Checks if the provided password is correct.
        
        :param pwd_to_check: The password to check
        :type pwd_to_check: str
        :return: True if the password is correct, False otherwise
        :rtype: bool
        """

        return sha512_crypt.verify(pwd_to_check, self.password)