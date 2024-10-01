using System;
using System.Collections.Generic;
using System.Data;
using Model;
using log4net;
using log4net.Util;

namespace Persistence
{
    public class UserDBRepository : IUserRepository
    {
        private static readonly ILog logger = LogManager.GetLogger("UsersDbRepo");

        IDictionary<String, string> props;

        public UserDBRepository(IDictionary<String, string> props)
        {
            logger.Info("Creating UserDBRepository");
            this.props = props;
        }

        public IEnumerable<User> findAll()
        {
            logger.Info("Finding all users");
            IList<User> users = new List<User>();
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id,name,password from users";
                using (var result = command.ExecuteReader())
                {
                    while (result.Read())
                    {
                        int id = result.GetInt32(0);
                        string name = result.GetString(1);
                        string password = result.GetString(2);
                        User user = new User(id, name, password);
                        users.Add(user);
                    }
                }
            }

            logger.InfoExt(users);
            return users;
        }

        public User findOne(int id)
        {
            logger.Info("Finding user with id " + id);
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id,name,password from users where id=@id";
                IDbDataParameter paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);
                using (var result = command.ExecuteReader())
                {
                    if (result.Read())
                    {
                        int idV = result.GetInt32(0);
                        string name = result.GetString(1);
                        string password = result.GetString(2);
                        User user = new User(idV, name, password);
                        logger.InfoExt(user);
                        return user;
                    }
                }
            }

            return null;
        }

        public User save(User entity)
        {
            throw new System.NotImplementedException();
        }

        public User delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public User update(User entity)
        {
            throw new System.NotImplementedException();
        }
    }
}